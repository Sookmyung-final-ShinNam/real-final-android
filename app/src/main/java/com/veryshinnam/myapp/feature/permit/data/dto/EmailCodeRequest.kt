package com.veryshinnam.myapp.feature.permit.data.dto

import com.google.gson.annotations.SerializedName

object EmailCodeRequest {

    data class Send(
        @SerializedName("tempCode") val tempCode: String,
        @SerializedName("email") val email: String
    )

    data class Verification(
        @SerializedName("tempCode") val tempCode: String,
        @SerializedName("code") val code: String
    )
}