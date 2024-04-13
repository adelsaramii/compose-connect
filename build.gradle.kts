buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
    }
}
plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin(Plugins.kmp) version Versions.kotlin apply false
    id(Plugins.android_application) version Versions.agp apply false
    id(Plugins.android_library) version Versions.agp apply false
    id(Plugins.compose) version Versions.compose apply false
    alias(libs.plugins.android.kotlin) apply false
}
