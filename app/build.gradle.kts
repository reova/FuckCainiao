plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "io.github.duzhaokun123.fuckcainiao"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.yuruichang.fuckcainiao"
        minSdk = 24
        targetSdk = 36
        versionCode = 13
        versionName = "0.2.5"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging.resources.excludes.add("**")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    compileOnly(libs.xposed.api)
    implementation(libs.ezXHelper)
    testImplementation(libs.junit)

}
