package com.veryshinnam.myapp.core.network

import com.google.gson.Gson
import retrofit2.HttpException

// 서버 에러 응답(BaseResponse)의 message 추출
// : 4xx/5xx 응답은 HttpException으로 던져지므로 errorBody에서 서버가 내려준 메시지를 읽는다
fun Throwable.serverMessage(default: String): String {
    if (this is HttpException) {
        val body = response()?.errorBody()?.string()
        val message = runCatching { Gson().fromJson(body, BaseResponse::class.java).message }.getOrNull()
        if (!message.isNullOrBlank()) return message
    }
    return default
}
