-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontwarn
-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-dontoptimize
-dontpreverify

-keepclasseswithmembernames class * {
 native <methods>;
}
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keep class com.customapi.fmkpl.datas.** {*;}
-keep class com.customapi.fmkpl.bean.** {*;}
-keep class com.customapi.fmkpl.okhttp.** {*;}
-keep class com.customapi.fmkpl.httpdata.** {*;}
-keep class com.github.fornewid.** {*;}
-keep class com.readystatesoftware.systembartint.** {*;}
-keep class com.airbnb.lottie.** { *; }

-keep class com.bea.xml.stream.** { *;}
-keep class com.wutka.dtd.** { *;}
-keep class org.** { *;}
-keep class aavax.xml.** { *;}
-keep class com.microsoft.schemas.office.x2006.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-keep class schemasMicrosoftComOfficeExcel.** { *; }
-keep class schemasMicrosoftComOfficeOffice.** { *; }
-keep class schemasMicrosoftComVml.** { *; }
-keep class repackage.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-keep class com.xuexiang.xupdate.entity.** { *; }
-keep class com.xuexiang.xupdatedemo.entity.** { *; }
-keep class com.baidu.location.** {*;}