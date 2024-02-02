package dev.jam.bunnyblocks;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import dev.jam.bunnyblocks.utils.CommonUtil;

public class App extends Application {
    public static App context;

    public void onCreate() {
        super.onCreate();
        context = this;
        CommonUtil.log("FirebaseApp.initializeApp finish");

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

    }

    public boolean isMainProcess() {
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo next : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (next.pid == myPid) {
                return getApplicationInfo().packageName.equals(next.processName);
            }
        }
        return false;
    }
}
