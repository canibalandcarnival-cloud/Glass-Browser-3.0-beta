package com.example.core

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"

// App Check contract keys. These strings are defined by the Firebase SDK and
// the runtime harness, so do not rename them.
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val TOKEN_CACHE_STORE_PREFIX = "com.google.firebase.appcheck.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
internal const val APPCHECK_BACKUP_STORE_NAME = "com.example.appcheck.backup"
internal const val APPCHECK_BACKUP_SECRET_KEY = "last_registered_debug_token"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

@Volatile
private var isProviderInstalled = false

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        // 1. Ensure FirebaseApp is initialized before configuring App Check
        val appContext = context.applicationContext ?: context
        if (FirebaseApp.getApps(appContext).isEmpty()) {
            FirebaseApp.initializeApp(appContext)
        }

        if (BuildConfig.DEBUG) {
            // 2. Debug: Read debug token from Intent extra if supplied by the emulator/test harness
            val intentDebugToken = (
                intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
                    ?: intent?.extras?.getString(DEBUG_TOKEN_EXTRA)
                )?.trim()?.takeIf { it.isNotEmpty() }

            val persistenceKey = FirebaseApp.getInstance().persistenceKey
            val debugPrefs = appContext.getSharedPreferences(
                "$DEBUG_STORE_PREFIX$persistenceKey",
                Context.MODE_PRIVATE
            )
            val backupPrefs = appContext.getSharedPreferences(
                APPCHECK_BACKUP_STORE_NAME,
                Context.MODE_PRIVATE
            )

            if (intentDebugToken != null) {
                // CRITICAL: MUST use synchronous commit() so the secret is persisted on disk
                // before DebugAppCheckProviderFactory reads it.
                debugPrefs.edit()
                    .putString(DEBUG_SECRET_KEY, intentDebugToken)
                    .commit()
                backupPrefs.edit()
                    .putString(APPCHECK_BACKUP_SECRET_KEY, intentDebugToken)
                    .commit()

                // Clear any stale cached App Check JWT so the next AI call exchanges a fresh valid token.
                appContext.getSharedPreferences(
                    "$TOKEN_CACHE_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                ).edit().clear().commit()

                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                isProviderInstalled = true
                FirebaseAppCheck.getInstance().getAppCheckToken(true)
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                val backupSecret = backupPrefs.getString(APPCHECK_BACKUP_SECRET_KEY, null)
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }

                if (backupSecret != null) {
                    // Re-opened inside an environment where a harness debug token was previously registered
                    // (e.g. tapping the Home Screen icon in the streaming emulator).
                    val existingDebugSecret = debugPrefs.getString(DEBUG_SECRET_KEY, null)
                    if (existingDebugSecret != backupSecret) {
                        debugPrefs.edit()
                            .putString(DEBUG_SECRET_KEY, backupSecret)
                            .commit()
                    }
                    if (!isProviderInstalled) {
                        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                            DebugAppCheckProviderFactory.getInstance()
                        )
                        isProviderInstalled = true
                        Log.d(TAG, "DebugAppCheckProviderFactory restored from registered token.")
                    }
                } else {
                    // Standalone physical Android phone running sideloaded app-debug.apk:
                    // No harness debug token exists, so a random DebugAppCheckProvider UUID would be
                    // rejected by Firebase with 403 Invalid App Check Token. Use the provisioned
                    // RecaptchaAppCheckProviderFactory (registered for com.aistudio.glassbrowser.vxqpmr
                    // and debug.keystore SHA-1/SHA-256) when recaptcha_site_key is available.
                    Log.w(TAG, "No debug token found in Intent extra; checking provisioned reCAPTCHA key for standalone APK.")
                    if (!isProviderInstalled) {
                        val siteKey = resolveRecaptchaSiteKey(appContext)
                        if (siteKey != null) {
                            // Evict any previously cached unregistered-UUID debug token from earlier installs
                            debugPrefs.edit().clear().commit()
                            appContext.getSharedPreferences(
                                "$TOKEN_CACHE_STORE_PREFIX$persistenceKey",
                                Context.MODE_PRIVATE
                            ).edit().clear().commit()

                            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                                RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                            )
                            isProviderInstalled = true
                            FirebaseAppCheck.getInstance().getAppCheckToken(true)
                            Log.d(TAG, "RecaptchaAppCheckProviderFactory installed for standalone debug APK.")
                        } else {
                            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                                DebugAppCheckProviderFactory.getInstance()
                            )
                            isProviderInstalled = true
                            Log.d(TAG, "DebugAppCheckProviderFactory installed as fallback.")
                        }
                    }
                }
            }
        } else {
            if (!isProviderInstalled) {
                // 3. Release: Look up recaptcha_site_key safely without throwing Resources.NotFoundException
                val siteKey = resolveRecaptchaSiteKey(appContext)
                if (siteKey != null) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    isProviderInstalled = true
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key string resource missing or blank.")
                }
            }
        }
    } catch (e: Exception) {
        // Log explicitly to keep App Check attestation failures visible
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

internal fun resolveRecaptchaSiteKey(context: Context): String? {
    val resId = context.resources.getIdentifier(
        RECAPTCHA_SITE_KEY_RES,
        "string",
        context.packageName
    )
    if (resId == 0) return null
    return context.getString(resId).trim().takeIf { it.isNotEmpty() }
}
