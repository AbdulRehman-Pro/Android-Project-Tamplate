package com.rehman.template.di

import com.rehman.template.data.ApiService
import com.rehman.template.data.RemoteDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    fun getRepository(
        apiService: ApiService
    ): RemoteDataRepository =
        RemoteDataRepository(apiService)

}