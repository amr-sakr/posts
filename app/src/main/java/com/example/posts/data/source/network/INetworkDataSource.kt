package com.example.posts.data.source.network

import com.example.posts.data.model.Post
import retrofit2.Response

interface INetworkDataSource {

    suspend fun getPosts(): List<Post>

    suspend fun createPost(body: Post): Response<Post>

    suspend fun editPost(postId: Int): Response<Post>

    suspend fun deletePost(postId: Int)

}