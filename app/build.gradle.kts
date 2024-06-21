plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.rarilabs.rarime"
    compileSdk = 34

    assetPacks += listOf(":zkp_assets")

    defaultConfig {

        applicationId = "com.rarilabs.rarime"
        minSdk = 27
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.1"

        externalNativeBuild {
            cmake {
                cppFlags += "-fexceptions -frtti -std=c++11"
                arguments += "-DANDROID_STL=c++_shared"
            }

            ndk {
                abiFilters += "arm64-v8a"
            }
        }

        resourceConfigurations.plus(listOf("en", "uk"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        create("debug_mainnet") {
            buildConfigField("Boolean", "isTestnet", "false")
        }
        create("release_mainnet") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField("Boolean", "isTestnet", "false")
        }
        create("debug_testnet") {
            buildConfigField("Boolean", "isTestnet", "true")
        }
        create("release_testnet") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField("Boolean", "isTestnet", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation("com.auth0.android:jwtdecode:2.0.2")

    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
//    implementation("moe.banana:moshi-jsonapi:master-SNAPSHOT")

    implementation("com.github.jeziellago:compose-markdown:0.5.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //// CAMERA STUFF ////
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view.v132beta02)
    implementation(libs.androidx.camera.extensions)

    //// ML-KIT ////
    implementation("com.google.mlkit:text-recognition:16.0.0")

    //// ACCOMPANIST ////
    implementation("com.google.accompanist:accompanist-permissions:0.31.6-rc")
    implementation("org.jmrtd:jmrtd:0.7.27")


    implementation("com.github.mhshams:jnbis:1.1.0")
    implementation("com.gemalto.jp2:jp2-android:1.0.3")

    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("com.google.dagger:hilt-android:2.51")
    implementation("androidx.navigation:navigation-compose:$lifecycle_version")
    kapt("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation("net.sf.scuba:scuba-sc-android:0.0.20")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(files("libs/Identity.aar"))

    // QR Code
    implementation("com.lightspark:compose-qr-code:1.0.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.airbnb.android:lottie-compose:6.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Web3
    implementation("org.web3j:core:4.9.8")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    implementation(libs.play.core)


}