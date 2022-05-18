package ru.turev.hiltcorrutinescicerone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.turev.hiltcorrutinescicerone.data.network.mapper.PhotoResponseMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MappersModule {

    @Singleton
    @Provides
    fun providePhotoResponseMapper() = PhotoResponseMapper()
}
