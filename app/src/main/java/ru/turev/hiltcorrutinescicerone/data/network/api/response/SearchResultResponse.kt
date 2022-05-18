package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName
import ru.turev.hiltcorrutinescicerone.data.network.api.response.PhotoResponse

data class SearchResultResponse(
    @SerializedName("results")
    val images: List<PhotoResponse>
)
