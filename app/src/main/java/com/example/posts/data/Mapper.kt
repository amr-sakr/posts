package com.example.posts.data

import com.example.posts.data.model.Post
import com.example.posts.data.source.local.PostEntity

fun PostEntity.toPost() = Post(
    id = id,
    title = title,
    body = body,
    userId = userId
)

fun Post.toEntity() = PostEntity(
    id = id ?: 0,
    title = title ?: "",
    body = body ?: "",
    userId = userId ?: 0
)

fun List<PostEntity>.toPostsListList() = map { it.toPost() }
