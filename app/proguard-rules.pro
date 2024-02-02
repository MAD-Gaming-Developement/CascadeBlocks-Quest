#region [ Default Required ]
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
#endregion

#region [ JavaScript Interface Classes ]
-keepclassmembers class dev.jam.bunnyblocks.utils.WebViewModel {
   public *;
}

-keepclassmembers class dev.jam.bunnyblocks.WebAct {
   public *;
}
#endregion

-keep class com.adjust.sdk.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.** { *; }