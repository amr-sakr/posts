package com.example.posts.data.source.network

import com.example.posts.data.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

const val PATH_POSTS = "posts"

interface PostsAPI {

    @GET(PATH_POSTS)
    suspend fun getPosts(): Response<List<Post>>

    @POST(PATH_POSTS)
    suspend fun createPost(@Body body: Post): Response<Post>

    @PUT("$PATH_POSTS/{id}")
    suspend fun editPost(@Path("id") postId: Int): Response<Post>


    @DELETE("$PATH_POSTS/{id}")
    suspend fun deletePost(@Path("id") postId: Int): Response<Unit>
}