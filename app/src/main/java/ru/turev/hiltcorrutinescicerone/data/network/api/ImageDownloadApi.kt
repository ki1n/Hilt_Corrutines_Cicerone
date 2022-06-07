package ru.turev.hiltcorrutinescicerone.data.network.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ImageDownloadApi {

    @GET
    suspend fun getBitmapFull(@Url urlFull: String): ResponseBody
}
