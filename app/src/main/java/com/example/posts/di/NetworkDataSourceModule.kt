package com.example.posts.di

import com.example.posts.data.source.network.INetworkDataSource
import com.example.posts.data.source.network.NetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindsPostsDataSource(dataSource: NetworkDataSource): INetworkDataSource
}