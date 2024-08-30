plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlinCompose)
}

android {
    namespace = "com.pedronveloso.digitalframe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pedronveloso.digitalframe"
        minSdk = 21
        targetSdk = 34
        versionCode = 12
        versionName = "0.0.12-DEV"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        // Flag to enable support for the new language APIs.
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = true
        checkAllWarnings = true
        warningsAsErrors = true
        htmlReport = true
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    // Android X Libraries.
    implementation(libs.coreKtx)
    implementation(libs.navigationCompose)
    implementation(libs.exifInterface)

    // ViewModel & Lifecycle.
    implementation(libs.lifecycleViewmodelKtx)
    implementation(libs.lifecycleViewmodelCompose)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.lifecycleViewmodelSavedstate)

    // Compose BOM.
    implementation(platform(libs.composeBom))
    implementation(libs.uiTooling)
    implementation(libs.uiToolingPreview)
    implementation(libs.ui)
    implementation(libs.material3)

    // Networking libraries.
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofitGson)

    // Image handling.
    implementation(libs.coilCompose)
    implementation(libs.paletteKtx)

    // Dependency Injection.
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    // Java 8 Desugaring. 🍬
    coreLibraryDesugaring(libs.desugarJdkLibs)

    // Import the BoM for the Firebase platform.
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseCrashlytics)

    // Dev Tooling.
    debugImplementation(libs.uiTooling)

    androidTestImplementation(libs.androidTestJunit)
    androidTestImplementation(libs.espressoCore)

    // Unit testing support.
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
