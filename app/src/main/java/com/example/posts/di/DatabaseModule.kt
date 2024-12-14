package com.example.posts.di

import android.content.Context
import androidx.room.Room
import com.example.posts.data.source.local.PostDao
import com.example.posts.data.source.local.PostsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun providePostsDatabase(
        @ApplicationContext context: Context,
    ): PostsDatabase = Room.databaseBuilder(
        context,
        PostsDatabase::class.java,
        "posts-database"
    )
        .fallbackToDestructiveMigration()
        .build()


    @Provides
    fun providesTopicsDao(
        database: PostsDatabase,
    ): PostDao = database.postDao()
}