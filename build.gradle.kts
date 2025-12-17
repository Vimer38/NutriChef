// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    configurations.matching { it.name == "classpath" }.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kapt) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}