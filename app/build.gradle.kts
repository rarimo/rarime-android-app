plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.rarilabs.rarime"
    compileSdk = 34

    bundle {
        language {
            enableSplit = false
        }
    }

    packaging {
        resources {
            pickFirsts.add("META-INF/DEPENDENCIES")
        }
    }

    assetPacks += listOf(":zkp_assets")

    defaultConfig {

        applicationId = "com.rarilabs.rarime"
        minSdk = 27
        targetSdk = 34

        versionCode = 65
        versionName = "2.1.14"

        externalNativeBuild {
            cmake {
                cppFlags += "-fno-stack-protector"
                arguments += "-DANDROID_STL=c++_shared"
            }

            ndk {
                //noinspection ChromeOsAbiSupport
                abiFilters += "arm64-v8a"
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }

        resourceConfigurations.plus(
            listOf(
                "en",
            )
        )

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
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        debug { }

        create("debug_mainnet") {
            initWith(getByName("debug"))
            buildConfigField("Boolean", "isTestnet", "false")
        }
        create("release_mainnet") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField("Boolean", "isTestnet", "false")
        }
        create("debug_testnet") {
            initWith(getByName("debug"))
            buildConfigField("Boolean", "isTestnet", "true")
        }
        create("release_testnet") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField("Boolean", "isTestnet", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        this.isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "/META-INF/DISCLAIMER"
            excludes += "/META-INF/DEPENDENCIES"
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
    implementation("io.coil-kt:coil-gif:2.6.0")
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
    implementation(libs.text.recognition)
    implementation("com.google.mlkit:segmentation-selfie:16.0.0-beta6")


    //// ACCOMPANIST ////
    implementation("com.google.accompanist:accompanist-permissions:0.31.6-rc")
    implementation("org.jmrtd:jmrtd:0.7.27")


    implementation("com.github.mhshams:jnbis:1.1.0")
    implementation("dev.keiji.jp2:jp2-android:1.0.4")

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
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("network.chaintech:qr-kit:1.0.6")
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


    implementation("com.google.android.play:asset-delivery:2.2.2")
    implementation("com.google.android.play:asset-delivery-ktx:2.2.2")

    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    //Room
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //google services
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-core:9.6.1")

    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation ("com.google.api-client:google-api-client:2.0.0")
    implementation ("com.google.api-client:google-api-client-android:1.32.1")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("androidx.hilt:hilt-work:1.0.0") // ?
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("com.google.firebase:firebase-messaging:24.0.1")

    // AppsFlyer
    implementation("com.appsflyer:af-android-sdk:6.15.1")
}
