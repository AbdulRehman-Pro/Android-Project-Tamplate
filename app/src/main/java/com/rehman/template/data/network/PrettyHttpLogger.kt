package com.rehman.template.data.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.logging.HttpLoggingInterceptor

class PrettyHttpLogger : HttpLoggingInterceptor.Logger {

    private var isResponseBody = false

    override fun log(message: String) {
        // Detect start of a response
        if (message.startsWith("<--")) {
            isResponseBody = true
            Log.i(TAG, message)
            return
        }

        // Detect end of response
        if (message.startsWith("--> END") || message.startsWith("<-- END")) {
            isResponseBody = false
            return
        }

        // Skip all request logs (like --> POST ... or headers)
        if (message.startsWith("-->")) return
        if (message.startsWith("Content-Type") || message.startsWith("Content-Length") || message.contains("=")) return

        // Pretty print JSON response body only
        if (isResponseBody &&
            (message.startsWith("{") && message.endsWith("}") ||
                    message.startsWith("[") && message.endsWith("]"))
        ) {
            try {
                val json = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = json.toJson(JsonParser.parseString(message))
                Log.i(TAG, "\n⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ API RESPONSE START ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇\n$prettyJson\n⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆ API RESPONSE END ⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆")
            } catch (e: Exception) {
                Log.i(TAG, message)
            }
        }
    }

    companion object {
        private const val TAG = "okhttp.OkHttpClient"
    }
}

