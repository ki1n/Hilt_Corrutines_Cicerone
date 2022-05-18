package ru.turev.hiltcorrutinescicerone.domain.errors

import retrofit2.HttpException

sealed class Resource<T> {

    data class Success<T>(val data: T) : Resource<T>()

    open class Error<T>(val exception: Exception) : Resource<T>()

    class NetworkError<T>(exception: Exception) : Error<T>(exception) {

        val code: Int? = (exception as? HttpException)?.code()
        val isServerError: Boolean = code?.let { code -> code / 100 == 5 } ?: false
    }
}
