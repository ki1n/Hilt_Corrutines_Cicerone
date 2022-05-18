package ru.turev.hiltcorrutinescicerone.data.network.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.turev.hiltcorrutinescicerone.BuildConfig
import ru.turev.hiltcorrutinescicerone.data.network.api.response.PhotoResponse
import ru.turev.hiltcorrutinescicerone.data.network.api.response.SearchResultResponse

interface PhotosDownloadApi {

    @Headers("Authorization: Client-ID ${BuildConfig.API_KEY}")
    @GET("/photos")
    suspend fun getAllPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<PhotoResponse>

    @Headers("Authorization: Client-ID ${BuildConfig.API_KEY}")
    @GET("/search/photos")
    suspend fun getSearchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int
    ): SearchResultResponse
}
