package ru.turev.hiltcorrutinescicerone.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.turev.hiltcorrutinescicerone.BuildConfig
import ru.turev.hiltcorrutinescicerone.data.network.api.ImageDownloadApi
import ru.turev.hiltcorrutinescicerone.data.network.api.PhotosDownloadApi
import ru.turev.hiltcorrutinescicerone.util.constants.Constants
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePhotosDownloadApi(retrofit: Retrofit): PhotosDownloadApi = retrofit.create(PhotosDownloadApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideRetrofitImage(
        okHttpClient: OkHttpClient,
        gson: Gson
    ) = Retrofit.Builder()
        .baseUrl("https://images.unsplash.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ImageDownloadApi::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor) = OkHttpClient.Builder()
        .readTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
        .connectTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()
}
