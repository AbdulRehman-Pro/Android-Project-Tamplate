package com.rehman.template.data.network

import android.util.Log
import com.rehman.template.core.enums.ApiTypes
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

abstract class ApiConverter {

    suspend fun <T> handleApiCall(
        apiCall: suspend () -> Response<T>,
        apiName: ApiTypes,
    ): Resource<T> {

        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body.let {
                    return Resource.Success(it, apiType = apiName.name)
                }
            }
            val message = response.errorBody()?.charStream()?.readText().toString()
            return Resource.Error(t = getErrorMessage(message), apiType = apiName.name)

        } catch (e: Exception) {
            return Resource.Error(t = e.localizedMessage, apiType = apiName.name)
        }

    }


    private fun getErrorMessage(apiResponse: String?): String? {
        try {
            val jObjError = JSONObject(apiResponse!!)
            when (val errorData = jObjError["message"]) {
                is JSONArray -> {

                    // It's an array
                    val jsonArray = jObjError.getJSONArray("message")
                    var message = ""

                    for (i in 0 until jsonArray.length()) {
                        message = if (i == 0)
                            jsonArray.getString(i)
                        else
                            message + "\n" + jsonArray.getString(i)
                    }

                    return message
                }

                is String -> {
                    // It's an object
                    return errorData.toString()
                }

                else -> return "Something went wrong Try again."
            }
        } catch (e: Exception) {
            Log.wtf("ApiConverter", "getErrorMessage: " + e.localizedMessage)
            return apiResponse
        }
    }

}