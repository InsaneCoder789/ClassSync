# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Google API client JSON models and field metadata intact in release builds.
# Gmail/Classroom responses are populated reflectively via @Key fields and GenericJson.
-keepattributes Signature,*Annotation*,EnclosingMethod,InnerClasses

-keep class com.google.api.client.util.Key { *; }
-keep class com.google.api.client.util.GenericData { *; }
-keep class com.google.api.client.json.GenericJson { *; }
-keep class com.google.api.client.googleapis.json.GoogleJsonError { *; }
-keep class com.google.api.client.googleapis.json.GoogleJsonError$ErrorInfo { *; }
-keep class com.google.api.client.json.gson.GsonFactory { *; }

-keep class com.google.api.services.gmail.model.** { *; }
-keep class com.google.api.services.classroom.model.** { *; }
-keep class com.google.api.client.googleapis.extensions.android.gms.auth.** { *; }

-keepclassmembers class * extends com.google.api.client.json.GenericJson {
    <fields>;
    <methods>;
}

-keepclassmembers class * {
    @com.google.api.client.util.Key <fields>;
}
