plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "exa.free.linuxbox"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "exa.free.linuxbox"
        minSdk = 24
        targetSdk = 37
        versionCode = 100
        versionName = "1.00 (Stable)"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.drawerlayout)
    implementation(libs.work.runtime)
    implementation(libs.annotation)
    implementation(libs.appcompat.v180)
    implementation(libs.core.splashscreen)
    implementation(libs.constraintlayout)
    implementation(libs.user.messaging.platform)
    implementation(libs.ads.mobile.sdk)

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}