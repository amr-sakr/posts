package com.example.posts.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface PostDao {

    @Query("SELECT * FROM post WHERE isDeleted = 0")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM post WHERE id = :postId")
    suspend fun getById(postId: Int): PostEntity?

    @Upsert
    suspend fun upsert(post: PostEntity)

    @Query("DELETE FROM post WHERE id = :postId")
    suspend fun deleteById(postId: Int): Int

    @Query("SELECT * FROM post WHERE isCreatedPostSynced = 0")
    suspend fun getNotSyncedPosts(): List<PostEntity>

    @Query("SELECT * FROM post WHERE isEditedPostSynced = 0")
    suspend fun getNotSyncedEditedPosts(): List<PostEntity>

    @Query("SELECT * FROM post WHERE isDeleted = 1")
    suspend fun getDeletedPosts(): List<PostEntity>

    @Query("UPDATE post SET isDeleted = 1, latestUpdate = :lastUpdated WHERE id = :postId")
    suspend fun markAsDeleted(postId: Int, lastUpdated: Long)
}
