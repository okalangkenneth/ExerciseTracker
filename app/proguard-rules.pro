# Add project specific ProGuard rules here.

# Keep names for better crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Kotlin Metadata
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Keep Compose rules
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep your application classes
-keep class com.healthtrack.exercise.** { *; }

# Keep Android lifecycle components
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Android runtime annotations
-keepattributes RuntimeVisible*Annotations*