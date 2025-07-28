package com.rehman.template.data.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class PrettyRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.method
        val url = request.url.toString()

        val sb = StringBuilder("\n⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆ API REQUEST START ⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆\n")
        sb.append("➡ Method: $method\n")
        sb.append("➡ URL: ${url.split("?").first()}\n")

        // Print query parameters line by line
        request.url.queryParameterNames.forEach { name ->
            val values = request.url.queryParameterValues(name)
            values.forEach { value ->
                sb.append("  ↪ $name = $value\n")
            }
        }

        // Print headers
        if (request.headers.size > 0) {
            sb.append("➡ Headers:\n")
            request.headers.forEach {
                sb.append("  ↪ ${it.first}: ${it.second}\n")
            }
        }

        // Print body for POST/PUT
        val requestBody = request.body
        if (requestBody != null && requestBody.contentLength() > 0) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val charset = requestBody.contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            val rawBody = buffer.readString(charset)

            sb.append("➡ Body:\n")

            if (requestBody.contentType()?.subtype == "json") {
                try {
                    val json = JsonParser.parseString(rawBody)
                    val prettyJson = GsonBuilder().setPrettyPrinting().create().toJson(json)
                    sb.append("$prettyJson\n")
                } catch (e: Exception) {
                    sb.append("$rawBody\n")
                }
            } else {
                rawBody.split("&").forEach { param ->
                    val parts = param.split("=")
                    if (parts.size == 2) sb.append("  ↪ ${parts[0]} = ${parts[1]}\n")
                    else sb.append("  ↪ $param\n")
                }
            }
        }

        sb.append("⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ API REQUEST END ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇\n")
        Log.i(TAG, sb.toString())

        return chain.proceed(request)
    }


    companion object {
        private const val TAG = "okhttp.OkHttpClient"
    }
}
