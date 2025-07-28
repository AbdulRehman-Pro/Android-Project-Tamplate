package com.rehman.template.data.network

sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null,
    val apiType: String? = null,
) {

    class Success<T>(data: T?, apiType: String? = null) : Resource<T>(data, apiType = apiType)
    class Error<T>(t: String?, data: T? = null, apiType: String? = null) : Resource<T>(data, t, apiType)
    class Loading<T>() : Resource<T>()


}