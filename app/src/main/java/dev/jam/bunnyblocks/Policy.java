package dev.jam.bunnyblocks;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class Policy extends AppCompatActivity {

    private WebView policy;
    private LinearLayout lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        policy = findViewById(R.id.policy);
        lin = findViewById(R.id.layout);

        policy.getSettings().setJavaScriptEnabled(true);

        policy.setWebChromeClient(new WebChromeClient());
        policy.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return false;
            }
        });

        policy.loadUrl("file:///android_asset/userconsent.html");


        policy.addJavascriptInterface(new JavaScriptInterface(), "Android");


    }


    private class JavaScriptInterface {
        @android.webkit.JavascriptInterface
        public void continueToGame() {

            moveToMainActivity();
        }

        @android.webkit.JavascriptInterface
        public void disagreeAndQuit() {

            finishAffinity();
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(Policy.this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
