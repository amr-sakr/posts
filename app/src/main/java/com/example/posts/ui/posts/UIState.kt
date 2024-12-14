package com.example.posts.ui.posts

import com.example.posts.data.model.Post

sealed class PostsState {
    object Loading : PostsState()
    data class Success(val posts: List<Post>) : PostsState()
    object Empty : PostsState()
    data class Error(val message: String) : PostsState()
}

sealed class DeletePostState {
    object Initial : DeletePostState()
    object Loading : DeletePostState()
    object Success : DeletePostState()
    data class Error(val message: String) : DeletePostState()
}

sealed class EditPostState {
    object Initial : EditPostState()
    object Loading : EditPostState()
    object Success : EditPostState()
    data class Error(val message: String) : EditPostState()
}

sealed class CreatePostState {
    object Initial : CreatePostState()
    object Loading : CreatePostState()
    data class Success(val message: String) : CreatePostState()
    data class Error(val message: String) : CreatePostState()
}