package ru.turev.hiltcorrutinescicerone.domain.errors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

interface ResourceHandler {

    suspend fun <T> resource(query: suspend () -> T): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(query())
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is IOException,
                    is HttpException -> Resource.NetworkError(e)
                    else -> Resource.Error(e)
                }
            }
        }
    }
}
