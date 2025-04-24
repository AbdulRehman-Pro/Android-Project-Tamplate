package com.rehman.template.core.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.Cipher
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import javax.crypto.spec.GCMParameterSpec

object CryptoUtils {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val GCM_TAG_LENGTH_BITS = 128


    fun createAESKeyIfNeeded(keyAlias: String) {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            if (!keyStore.containsAlias(keyAlias)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )
                val keyGenSpec = KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()

                keyGenerator.init(keyGenSpec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            Log.e("CryptoUtils", "Key generation failed", e)
        }
    }


    // First argument should be string and for Int use (Int.toString()) , for Boolean use (Boolean.toString()) and non custom data use encryptObject and decryptObject
    fun encrypt(text: String, keyAlias: String): Pair<String, String>? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            val secretKeyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
            val secretKey = secretKeyEntry?.secretKey ?: return null

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

            val encryptedText = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            val ivString = Base64.encodeToString(iv, Base64.NO_WRAP)

            encryptedText to ivString
        } catch (e: Exception) {
            Log.e("CryptoUtils", "Encryption failed", e)
            null
        }
    }


    fun decrypt(encryptedText: String, ivString: String, keyAlias: String): String? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            val secretKeyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
            val secretKey = secretKeyEntry?.secretKey ?: return null

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = Base64.decode(ivString, Base64.NO_WRAP)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

            val encryptedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e("CryptoUtils", "Decryption failed", e)
            null
        }
    }


    fun deleteKey(keyAlias: String) {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(keyAlias)) {
                keyStore.deleteEntry(keyAlias)
            }
        } catch (e: Exception) {
            Log.e("CryptoUtils", "Failed to delete AES key", e)
        }
    }

    // 🔹 Generic object encryption (serialize to JSON)
    inline fun <reified T> encryptObject(obj: T, keyAlias: String): Pair<String, String>? {
        val json = Gson().toJson(obj)
        return encrypt(json, keyAlias)
    }

    // 🔹 Generic object decryption (deserialize from JSON)
    inline fun <reified T> decryptObject(encrypted: String, iv: String, keyAlias: String): T? {
        val json = decrypt(encrypted, iv, keyAlias)
        return json?.let { Gson().fromJson(it, T::class.java) }
    }

}
