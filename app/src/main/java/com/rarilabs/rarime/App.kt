package com.rarilabs.rarime

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.rarilabs.rarime.config.Keys
import dagger.hilt.android.HiltAndroidApp
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@HiltAndroidApp
class App: Application() {
    private fun setupFireBase() {
        val options = FirebaseOptions.Builder()
            .setApplicationId(Keys.ApplicationId) // Required for Analytics.
            .setProjectId(Keys.ProjectId) // Required for Firebase Installations.
            .setApiKey(Keys.ApiKey) // Required for Auth.
            .build()
        FirebaseApp.initializeApp(this, options, "United Space")
    }

    override fun onCreate() {
        super.onCreate()
        setupBouncyCastle()
        setupFireBase()
    }

    private fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up the provider lazily when it's first used.
            return
        if (provider::class.java.equals(BouncyCastleProvider::class.java)) {
            // BC with same package name, shouldn't happen in real life.
            throw IllegalStateException("BC with same package name")
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}