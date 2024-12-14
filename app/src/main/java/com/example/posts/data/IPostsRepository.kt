package com.example.posts.data

import com.example.posts.data.model.Post
import kotlinx.coroutines.flow.Flow

interface IPostsRepository {

    fun getPosts(): Flow<List<Post>>

    suspend fun createPost(post: Post)

    suspend fun syncQueuedPosts()

    suspend fun editPost(post: Post)

    suspend fun syncQueuedEditedPosts()

    suspend fun deletePost(postId: Int)

    suspend fun syncQueuedDeletedPosts()
}
