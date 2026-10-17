import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// Release signing is enabled only when the protected CI environment supplies all
// signing values and the keystore file exists. Local builds and ordinary CI builds
// remain unsigned release builds; never commit the keystore or passwords.
val releaseKeystorePath = System.getenv("ANDROID_UPLOAD_KEYSTORE_PATH")
val releaseKeystorePassword = System.getenv("ANDROID_UPLOAD_KEYSTORE_PASSWORD")
val releaseKeyAlias = System.getenv("ANDROID_UPLOAD_KEY_ALIAS")
val releaseKeyPassword = System.getenv("ANDROID_UPLOAD_KEY_PASSWORD")
val releaseSigningConfigured =
    !releaseKeystorePath.isNullOrBlank() &&
        File(releaseKeystorePath).isFile &&
        !releaseKeystorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank()

android {
    namespace = "nl.draveinx.klaarkompas"
    compileSdk = 36

    defaultConfig {
        applicationId = "nl.draveinx.klaarkompas"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "0.3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    flavorDimensions += "formFactor"
    productFlavors {
        create("phone") {
            dimension = "formFactor"
            applicationIdSuffix = ".phone"
            versionNameSuffix = "-phone"
            resValue("string", "app_name", "KlaarKompas Telefoon")
        }
        create("tablet") {
            dimension = "formFactor"
            applicationIdSuffix = ".tablet"
            versionNameSuffix = "-tablet"
            resValue("string", "app_name", "KlaarKompas Tablet")
        }
    }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = File(requireNotNull(releaseKeystorePath))
                storePassword = requireNotNull(releaseKeystorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
