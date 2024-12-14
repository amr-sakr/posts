package com.example.posts.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.posts.data.IPostsRepository
import com.example.posts.data.model.Post
import com.example.posts.data.source.network.worker.CreatePostWorker
import com.example.posts.data.source.network.worker.DeletePostWorker
import com.example.posts.data.source.network.worker.EditPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: IPostsRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _createPostState = MutableStateFlow<CreatePostState>(CreatePostState.Initial)
    val createPostState: StateFlow<CreatePostState> = _createPostState

    private val _editPostState = MutableStateFlow<EditPostState>(EditPostState.Initial)
    val editPostState: StateFlow<EditPostState> = _editPostState

    private val _deletePostState = MutableStateFlow<DeletePostState>(DeletePostState.Initial)
    val deletePostState: StateFlow<DeletePostState> = _deletePostState

    var editPostArg: Post? = null

    val postsList: StateFlow<PostsState> = repository.getPosts()
        .map { posts ->
            if (posts.isNotEmpty()) {
                PostsState.Success(posts)
            } else {
                PostsState.Empty
            }
        }
        .catch { e ->
            emit(PostsState.Error(e.message ?: "Something went wrong"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PostsState.Loading
        )

    fun createPost(post: Post) {
        viewModelScope.launch {
            _createPostState.value = CreatePostState.Loading
            try {
                repository.createPost(post)
                _createPostState.value = CreatePostState.Success("Post created")
                syncCreatePostWorker()
            } catch (e: Exception) {
                _createPostState.value = CreatePostState.Error("Error creating post-> ${e.message}")
            }
        }
    }

    fun editPost(post: Post) {
        viewModelScope.launch {
            _editPostState.value = EditPostState.Loading
            try {
                repository.editPost(post)
                _editPostState.value = EditPostState.Success
                syncEditPostWorker()
            } catch (e: Exception) {
                _editPostState.value = EditPostState.Error("Error editing the post-> ${e.message}")
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _deletePostState.value = DeletePostState.Loading
            try {
                repository.deletePost(postId)
                _deletePostState.value = DeletePostState.Success
                syncDeletedPostsWorker()
            } catch (e: Exception) {
                _deletePostState.value = DeletePostState.Error("Error deleting post-> ${e.message}")
            }
        }
    }

    private fun syncDeletedPostsWorker() {
        val workRequest = OneTimeWorkRequestBuilder<DeletePostWorker>().build()
        workManager.enqueueUniqueWork(
            "sync_deleted_posts",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun syncCreatePostWorker() {
        val workRequest = OneTimeWorkRequestBuilder<CreatePostWorker>().build()
        workManager.enqueueUniqueWork(
            "sync_created_posts",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun syncEditPostWorker() {
        val workRequest = OneTimeWorkRequestBuilder<EditPostWorker>().build()
        workManager.enqueueUniqueWork(
            "sync_edited_posts",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    fun resetCreatePostState() {
        _createPostState.value = CreatePostState.Initial
    }

    fun resetDeletePostState() {
        _deletePostState.value = DeletePostState.Initial
    }

    fun resetEditPostState() {
        _editPostState.value = EditPostState.Initial
    }
}