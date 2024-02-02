package dev.jam.bunnyblocks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.*;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.*;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.gson.Gson;
import dev.jam.bunnyblocks.utils.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class WebAct extends BaseActivity implements View.OnClickListener {

    private boolean canExit = false;
    private String deepLinkUrl = "";
    private final Handler handler_exit = new Handler(Looper.myLooper());
    private boolean isHome = false;
    private View loading_layer;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private WebAct mContext;
    private WebView mWebView;
    private SignInClient oneTapClient;
    private PopupWindow popupWindow_html;
    private BeginSignInRequest signInRequest;
    private Runnable task_exit = () -> {
        boolean unused = WebAct.this.canExit = false;
    };
    private TextView text_app_version;
    private ViewGroup view_panel;
    private WebViewModel webViewModel;
    private String apiResponse;
    private String appStatus;
    private String webURL;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        setContentView(R.layout.activity_web);
        getWindow().addFlags(128);

        initRemoteConfig();
        initAdjust();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public void initRemoteConfig() {
        handleApiRequest(this);

    }

    private void initAdjust() {
        if (AppEventsConstants.EVENT_PARAM_VALUE_YES.equals(getString(R.string.adjust_switch))) {
            String string = getString(R.string.adjust_app_token);
            String str = CommonUtil.isDebugEnv(context) ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
            CommonUtil.log("AdjustConfig Env = " + str);
            Adjust.onCreate(new AdjustConfig(this, string, str));
            CommonUtil.log("AdjustConfig Adid = " + Adjust.getAdid());
        }
    }

    public void init(String str) {
        this.view_panel = (ViewGroup) findViewById(R.id.webview_layout);
        this.loading_layer = findViewById(R.id.loading_layer);
        this.text_app_version = (TextView) findViewById(R.id.app_version);
        this.text_app_version.setText(CommonUtil.getAppVersionName(this.mContext));
        WebAct webActivity = this.mContext;
        this.webViewModel = new WebViewModel(webActivity, str, new LocJS2Android(webActivity), new WebViewModel.WebLoadStatus() {
            public void progress(int i) {
            }

            public void onFinish() {
                WebAct.this.mContext.loading_layer.setVisibility(View.GONE);
            }
        });
        this.view_panel.addView(this.webViewModel.getContentView(), 0, new RelativeLayout.LayoutParams(-1, -1));
        this.mWebView = this.webViewModel.getWebview();
        this.mCallbackManager = CallbackManager.Factory.create();
        this.loginButton = (LoginButton) findViewById(R.id.login_button);
        this.loginButton.setPermissions("email", "public_profile");

        ((Button) findViewById(R.id.click_share)).setOnClickListener(view -> {
            ShareLinkContent build = ((ShareLinkContent.Builder) new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://www.google.cn/"))).build();
            ShareDialog shareDialog = new ShareDialog((Activity) WebAct.this.mContext);
            shareDialog.registerCallback(WebAct.this.mCallbackManager, new FacebookCallback<Sharer.Result>() {
                public void onSuccess(Sharer.Result result) {
                    CommonUtil.log("Sharer.Result onSuccess");
                }

                public void onError(FacebookException facebookException) {
                    CommonUtil.log("Sharer.Result onError " + facebookException.toString());
                }

                public void onCancel() {
                    CommonUtil.log("Sharer.Result onCancel");
                }
            });
            shareDialog.show(build, ShareDialog.Mode.AUTOMATIC);
        });
    }

    public void sendDeepLinkUrl() {
        CommonUtil.log("sendDeepLinkUrl:" + this.deepLinkUrl);
        String str = "onDeepLinkUrlCallback('" + this.deepLinkUrl + "')";
        WebViewModel webViewModel2 = this.webViewModel;
        if (webViewModel2 != null) {
            webViewModel2.callJS(str);
        }
    }

    public void handleFacebookAccessToken(AccessToken accessToken) {
        CommonUtil.log("handleFacebookAccessToken:" + accessToken);
    }


    @Override
    public void onResume() {
        super.onResume();
        Adjust.onResume();
        boolean z = this.isHome;
        this.isHome = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Adjust.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.isHome = true;
    }

    private void handleSignInResult(SignInCredential signInCredential) {
        String googleIdToken = signInCredential.getGoogleIdToken();
        String id = signInCredential.getId();
        signInCredential.getPassword();
        if (googleIdToken != null) {
            CommonUtil.log("Got ID token === " + googleIdToken);
        } else if (id != null) {
            CommonUtil.log("Got username === " + id);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123456) {
            try {
                handleSignInResult(this.oneTapClient.getSignInCredentialFromIntent(intent));
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {
            CallbackManager callbackManager = this.mCallbackManager;
            if (callbackManager != null) {
                callbackManager.onActivityResult(i, i2, intent);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void onClick(View view) {
        view.getId();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showHtmlContentView(String str) {
        Method method;
        PopupWindow popupWindow = this.popupWindow_html;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.popupWindow_html.dismiss();
            this.popupWindow_html = null;
        }
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.view_simp_webview, (ViewGroup) null);
        this.popupWindow_html = new PopupWindow(inflate, -1, -1);
        this.popupWindow_html.setBackgroundDrawable(new BitmapDrawable());
        this.popupWindow_html.setFocusable(true);
        this.popupWindow_html.setTouchable(true);
        this.popupWindow_html.setOutsideTouchable(true);
        this.popupWindow_html.setAnimationStyle(R.style.popupAnimation_dialog);
        this.popupWindow_html.showAtLocation(getWindow().getDecorView(), 0, 0, 0);
        final ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.progress);
        WebView webView = (WebView) inflate.findViewById(R.id.webview);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDatabaseEnabled(true);
        try {
            if (Build.VERSION.SDK_INT >= 16 && (method = webView.getSettings().getClass().getMethod("setAllowUniversalAccessFromFileURLs", new Class[]{Boolean.TYPE})) != null) {
                method.invoke(webView.getSettings(), new Object[]{true});
            }
        } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setMixedContentMode(2);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i <= 70) {
                    if (progressBar.getVisibility() != View.VISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(i);
                } else if (progressBar.getVisibility() != View.GONE) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (TextUtils.isEmpty(str)) {
                    return true;
                }
                webView.loadUrl(str);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }
        });
        webView.loadData(str, "text/html", "utf-8");
    }

    public void handleApiRequest(Activity context) {
        RequestQueue connectAPI = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();

        String endPoint = getString(R.string.app_api_url) + getString(R.string.app_id) + "&package=" + context.getPackageName();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    try {
                        apiResponse = response.toString();
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = MCryptHelper.decrypt(jsonData.getString("data"), "21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);

                        appStatus = jsonData.getString("gameKey");
                        webURL = gameData.getString("gameURL");

                        handleApiResponse(context);

                        CommonUtil.log("Web URL: " + webURL);

                    } catch (JSONException e) {
                        CommonUtil.log("Error parsing JSON: " + e.getMessage());
                    } catch (Exception e) {
                        CommonUtil.log("Error processing API response: " + e.getMessage());
                    }
                },
                error -> CommonUtil.log("API request failed: " + error.toString()));

        connectAPI.add(jsonRequest);
    }

    private void handleApiResponse(Activity context) {
        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
            if (Boolean.parseBoolean(appStatus)) {

                SharedPreferences appPreferences = context.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                appPreferences.edit().putBoolean("APPSTATS", Boolean.parseBoolean(appStatus)).apply();
                appPreferences.edit().putString("GAMEURL", webURL).apply();
                WebAct.this.init(webURL);

            } else {
                Intent intent;
                intent = new Intent(context, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.finish();
            }
        }, 1200);
    }


    private class LocJS2Android extends WebViewModel.JS2Android {
        public LocJS2Android(Activity activity) {
            super(activity);
        }

        @JavascriptInterface
        public void setData(String str, String str2) {
            LocalStorageHelper.getInstance(getActivity()).putString(str, str2);
        }

        @JavascriptInterface
        public void removeData(String str) {
            LocalStorageHelper.getInstance(getActivity()).removeString(str);
        }

        @JavascriptInterface
        public void login_facebook() {
            CommonUtil.log("login_facebook");
            if (!CommonUtil.checkAppInstalled(WebAct.this.mContext, "com.facebook.katana")) {
                CommonUtil.openGooglePlay(WebAct.this.mContext, "com.facebook.katana");
                return;
            }
            final AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
            if (currentAccessToken != null && !currentAccessToken.isExpired()) {
                new Thread(() -> WebAct.this.mContext.webViewModel.callJS("login_facebook(0,'" + currentAccessToken.getToken() + "')")).start();
            } else {
                WebAct.this.mContext.runOnUiThread(() -> {
                    if (WebAct.this.mContext.loginButton != null) {
                        WebAct.this.mContext.loginButton.performClick();
                    }
                });
            }
        }

        @JavascriptInterface
        public void logout_facebook() {
            CommonUtil.log("logout_facebook");
            AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
            if (currentAccessToken != null && !currentAccessToken.isExpired()) {
                LoginManager.getInstance().logOut();
            }
        }

        @JavascriptInterface
        public void sharelink_facebook(final String str) {
            CommonUtil.log("sharelink_facebook : " + str);
            if (!CommonUtil.checkAppInstalled(WebAct.this.mContext, "com.facebook.katana")) {
                CommonUtil.openGooglePlay(WebAct.this.mContext, "com.facebook.katana");
            } else {
                WebAct.this.mContext.runOnUiThread(() -> {
                    ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
                    ShareLinkContent build = ((ShareLinkContent.Builder) builder.setContentUrl(Uri.parse("" + str))).build();
                    ShareDialog shareDialog = new ShareDialog((Activity) WebAct.this.mContext);
                    shareDialog.registerCallback(WebAct.this.mCallbackManager, new FacebookCallback<Sharer.Result>() {
                        public void onSuccess(Sharer.Result result) {
                            CommonUtil.log("Sharer.Result onSuccess");
                        }

                        public void onError(FacebookException facebookException) {
                            CommonUtil.log("Sharer.Result onError " + facebookException.toString());
                        }

                        public void onCancel() {
                            CommonUtil.log("Sharer.Result onCancel");
                        }
                    });
                    shareDialog.show((ShareContent<?, ?>) build, ShareDialog.Mode.AUTOMATIC);
                });
            }
        }

        @JavascriptInterface
        public void getDeepLinkUrl() {
            CommonUtil.log("getDeepLinkUrl()");
            WebAct.this.sendDeepLinkUrl();
        }

        @JavascriptInterface
        public void getAppInfo() {
            CommonUtil.log("native.getAppInfo()");
            String deviceId = DeviceIdUtil.getDeviceId(WebAct.this.mContext);
            String appVersionName = CommonUtil.getAppVersionName(WebAct.this.mContext);
            String channleID = CommonUtil.getChannleID();
            String string = LocalStorageHelper.getInstance(WebAct.this.mContext).getString("identity");
            String string2 = LocalStorageHelper.getInstance(WebAct.this.mContext).getString(AnalyticsEvents.PARAMETER_LIKE_VIEW_STYLE);
            String string3 = LocalStorageHelper.getInstance(WebAct.this.mContext).getString("parent_id");
            AppInfoModel appInfoModel = new AppInfoModel();
            appInfoModel.setDevice_id(deviceId);
            appInfoModel.setApp_version(appVersionName);
            appInfoModel.setChannel_id(channleID);
            appInfoModel.setIdentity(string);
            appInfoModel.setStyle(string2);
            if (!TextUtils.isEmpty(string3)) {
                appInfoModel.setParent_id(string3);
            }
            String str = "nativeCallback.getAppInfo('" + new Gson().toJson((Object) appInfoModel) + "')";
            WebAct.this.mContext.webViewModel.callJS(str);
            CommonUtil.log(str);
        }

        @JavascriptInterface
        public void saveQRCode(String str) {
            if (!TextUtils.isEmpty(str)) {
                try {
                    if (str.startsWith("data:image/png;base64,")) {
                        str = str.replace("data:image/png;base64,", "");
                    }
                    byte[] decode = Base64.decode(str);
                    Bitmap decodeByteArray = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                    MediaStore.Images.Media.insertImage(WebAct.this.getContentResolver(), decodeByteArray, "title", "description");
                    decodeByteArray.recycle();
                    WebAct.this.mContext.webViewModel.callJS("nativeCallback.saveQRCode('0')");
                } catch (Exception unused) {
                    WebAct.this.mContext.webViewModel.callJS("nativeCallback.saveQRCode('-1')");
                }
            }
        }

        @JavascriptInterface
        public void openURL(String str) {
            CommonUtil.log("native.openURL()");
            if (!TextUtils.isEmpty(str)) {
                if (!str.toLowerCase().startsWith("http")) {
                    str = "https://" + str;
                }
                WebAct.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            }
        }

        @JavascriptInterface
        public void openWebView(final String str) {
            CommonUtil.log("native.openWebView() " + str);
            if (!TextUtils.isEmpty(str)) {
                WebAct.this.mContext.runOnUiThread(() -> WebAct.this.showHtmlContentView(str));
            }
        }

        @JavascriptInterface
        public void trackAdjustEvent(String str) {
            CommonUtil.log("native.AdjustTrackEvent() " + str);
            if (AppEventsConstants.EVENT_PARAM_VALUE_YES.equals(WebAct.this.getString(R.string.adjust_switch))) {
                Adjust.trackEvent(new AdjustEvent(str));
            }
        }

        @JavascriptInterface
        public void onEventJs(String str) {
            CommonUtil.log("native.AdjustEvent() " + str);
            if (AppEventsConstants.EVENT_PARAM_VALUE_YES.equals(WebAct.this.getString(R.string.adjust_switch))) {
                Adjust.trackEvent(new AdjustEvent(WebAct.this.getString(R.string.adjust_event_register_success)));
            }
        }

        @JavascriptInterface
        public void onEventJsRecharge(String str) {
            CommonUtil.log("native.AdjustEvent() " + str);
            if (AppEventsConstants.EVENT_PARAM_VALUE_YES.equals(WebAct.this.getString(R.string.adjust_switch))) {
                Adjust.trackEvent(new AdjustEvent(WebAct.this.getString(R.string.adjust_event_recharge_success)));
            }
        }

        @JavascriptInterface
        public void onEventJsFirstRecharge(String str) {
            CommonUtil.log("native.AdjustEvent() " + str);
            if (AppEventsConstants.EVENT_PARAM_VALUE_YES.equals(WebAct.this.getString(R.string.adjust_switch))) {
                Adjust.trackEvent(new AdjustEvent(WebAct.this.getString(R.string.adjust_event_first_recharge_success)));
            }
        }
    }

    private String buildTransaction(String str) {
        if (str == null) {
            return String.valueOf(System.currentTimeMillis());
        }
        return str + System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        WebView webView = this.mWebView;
        if (webView == null || !webView.canGoBack()) {
            if (this.canExit) {
                exit();
            } else {
                this.canExit = true;
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{10, 100}, -1);
                CommonUtil.alert("will exit!");
                this.handler_exit.postDelayed(this.task_exit, 2000);
            }
            return true;
        }
        this.mWebView.goBack();
        return true;
    }

    private void exit() {
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        System.exit(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.log("WebActivity onDestroy~");
    }
}