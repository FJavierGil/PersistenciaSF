plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "es.upm.miw.persistencia2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "es.upm.miw.persistencia2"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
}