package com.example.posts.data.source.network

import com.example.posts.data.model.Post
import retrofit2.Response
import javax.inject.Inject

class NetworkDataSource @Inject constructor(
    private val apiCalls: PostsAPI
) : INetworkDataSource {
    override suspend fun getPosts(): List<Post> {
        val response = apiCalls.getPosts()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Something went wrong-> ${response.message()}")
        }
    }

    override suspend fun createPost(body: Post) = apiCalls.createPost(body)

    override suspend fun editPost(postId: Int): Response<Post> = apiCalls.editPost(postId)

    override suspend fun deletePost(postId: Int) {
        apiCalls.deletePost(postId)
    }

}