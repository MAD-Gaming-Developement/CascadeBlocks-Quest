package dev.jam.bunnyblocks.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import dev.jam.bunnyblocks.R;

public class BaseActivity extends Activity {
    protected static BaseActivity context;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = this;
    }

    public void back(Activity activity) {
        CommonUtil.hideCurrActivitySoftInput(activity);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        return true;
    }

}
