package com.example.posts.di

import com.example.posts.data.IPostsRepository
import com.example.posts.data.PostsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindPostsRepository(repository: PostsRepository): IPostsRepository
}
