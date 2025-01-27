// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    kotlin("jvm").version(libs.versions.kotlin).apply(false)
    kotlin("plugin.serialization").version(libs.versions.kotlin).apply(false)
}