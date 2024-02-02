package dev.jam.bunnyblocks.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import dev.jam.bunnyblocks.App;

import java.util.Random;
import java.util.regex.Pattern;

public class CommonUtil {

    public static boolean isEmojiCharacter(char c) {
        return (c == 0 || c == 9 || c == 10 || c == 13 || (c >= ' ' && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 0 && c <= 65535))) ? false : true;
    }

    public static void log(String str) {
        Log.w("dev_log", " " + str);
    }


    public static void alert(String str) {
        if (str == null) {
            log("CommonUtil->alert msg is null");
            str = "~No msg";
        }
        App app = App.context;
        Toast.makeText(app, " " + str, Toast.LENGTH_SHORT).show();
    }


    public static boolean isDebugEnv(Context context) {
        return (context.getApplicationInfo().flags & 2) != 0;
    }


    public static void hideCurrActivitySoftInput(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            try {
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus != null) {
                    ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
                }
            } catch (Exception unused) {
            }
        }
    }


    public static boolean checkAppInstalled(Context context, String str) {
        PackageInfo packageInfo;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        }
        return true;
    }

    public static void openGooglePlay(Context context, String str) {
        try {
            if (context.getPackageName() != null) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str));
                intent.setPackage(str);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + str));
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String getChannleID() {
        try {
            ApplicationInfo applicationInfo = App.context.getPackageManager().getApplicationInfo(App.context.getPackageName(), 128);
            return applicationInfo.metaData.getString("channel_id") + "";
        } catch (Exception unused) {
            return "";
        }
    }


    public static int getRandom(int i, int i2) {
        return new Random().nextInt((i2 - i) + 1) + i;
    }

    public static void toSelfSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), (String) null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public static boolean isWebviewUA(String str) {
        return Pattern.compile("(" + String.join("|", new String[]{"WebView", "Android.*(wv|\\.0\\.0\\.0)"}) + ")", 2).matcher(str).find();
    }

}
