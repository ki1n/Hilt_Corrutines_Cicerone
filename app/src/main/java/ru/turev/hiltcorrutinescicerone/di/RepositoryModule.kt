package ru.turev.hiltcorrutinescicerone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.turev.hiltcorrutinescicerone.data.network.api.ApiService
import ru.turev.hiltcorrutinescicerone.data.network.mapper.PhotoResponseMapper
import ru.turev.hiltcorrutinescicerone.data.repository.PhotoRepositoryImpl
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAllPhotos(
        apiService: ApiService,
        photoResponseMapper: PhotoResponseMapper
    ): PhotoRepository = PhotoRepositoryImpl(apiService, photoResponseMapper)
}
