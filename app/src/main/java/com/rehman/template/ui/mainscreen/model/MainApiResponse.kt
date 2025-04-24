package com.rehman.template.ui.mainscreen.model

import com.google.gson.annotations.SerializedName

data class MainApiResponse(

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: Data = Data()

) {
    data class Data(
        @SerializedName("users")
        val users: List<UserData> = emptyList()
    ) {
        data class UserData(
            @SerializedName("id")
            val id: Int? = null,

            @SerializedName("name")
            val name: String? = null
        )
    }
}
