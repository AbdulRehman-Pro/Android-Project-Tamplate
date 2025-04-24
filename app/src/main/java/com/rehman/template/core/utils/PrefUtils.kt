package com.rehman.template.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

object PrefUtils {

    private const val TAG = "PrefUtils"

    private const val APP_NAME = "AndroidApplication"

    private const val AUTH_TOKEN_KEY = "auth_token"
    private const val IV_KEY = "iv"
    private const val REMEMBER_ME_KEY = "is_remember_me"


    // Key Alias
    private const val AUTH_TOKEN_ALIAS = "auth_token_key_alias"


    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    }


    // Auth token handling with encryption and decryption functionality
    fun saveAuthToken(context: Context, token: String) {
        CryptoUtils.createAESKeyIfNeeded(AUTH_TOKEN_ALIAS)
        val result = CryptoUtils.encrypt(token, AUTH_TOKEN_ALIAS)
        if (result != null) {
            val (encryptedToken, iv) = result

            getSharedPreferences(context).edit() {
                putString(AUTH_TOKEN_KEY, encryptedToken)
                putString(IV_KEY, iv)
            }
        } else {
            Log.e(TAG, "Token encryption failed, not saving token")
        }


    }

    fun getAuthToken(context: Context): String? {
        val prefs = getSharedPreferences(context)
        val encryptedToken = prefs.getString(AUTH_TOKEN_KEY, null)
        val iv = prefs.getString(IV_KEY, null)

        return if (encryptedToken != null && iv != null) {
            CryptoUtils.decrypt(encryptedToken, iv, AUTH_TOKEN_ALIAS)
        } else null
    }

    fun clearAuthToken(context: Context) {
        CryptoUtils.deleteKey(AUTH_TOKEN_ALIAS)
        getSharedPreferences(context).edit() {
            remove(AUTH_TOKEN_KEY)
            remove(IV_KEY)
        }
    }


    // Remember Me
    fun setIsRememberMe(context: Context, isRememberMe: Boolean) {
        getSharedPreferences(context).edit() { putBoolean(REMEMBER_ME_KEY, isRememberMe) }
    }

    fun getIsRememberMe(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(REMEMBER_ME_KEY, false)
    }

    fun clearIsRememberMe(context: Context) {
        getSharedPreferences(context).edit() { remove(REMEMBER_ME_KEY) }
    }


}
