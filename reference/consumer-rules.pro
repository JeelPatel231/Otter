-keep class okhttp3.OkHttpClient
-keep class kotlin.jvm.internal.Intrinsics
-keepclassmembers class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNullParameter(...);
}

-keep class tel.jeelpa.otter.reference.** { *; }