package com.example.posts.data

import com.example.posts.data.model.Post
import com.example.posts.data.source.local.PostDao
import com.example.posts.data.source.local.PostEntity
import com.example.posts.data.source.network.INetworkDataSource
import com.example.posts.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostsRepository @Inject constructor(
    private val networkDataSource: INetworkDataSource,
    private val localDataSource: PostDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IPostsRepository {

    override fun getPosts(): Flow<List<Post>> = flow {
        try {
            networkDataSource.getPosts()
                .map { it.toEntity() }
                .forEach { post ->
                    post.isCreatedPostSynced = true
                    post.isEditedPostSynced = true
                    localDataSource.upsert(post)
                }
            emitAll(localDataSource.getAllPosts().map { it.toPostsListList() })
        } catch (e: Exception) {
            println(e.stackTrace)
            emitAll(localDataSource.getAllPosts().map { it.toPostsListList() })
        }
    }.catch { e ->
        emit(emptyList())
    }.flowOn(ioDispatcher)


    override suspend fun createPost(post: Post) {
        val postEntity = PostEntity(
            id = post.id ?: 0,
            body = post.body ?: "",
            title = post.title ?: "",
            userId = post.userId ?: 0,
            isCreatedPostSynced = false,
            latestUpdate = System.currentTimeMillis()
        )

        localDataSource.upsert(postEntity)
        syncCreatePost(postEntity)
    }

    private suspend fun syncCreatePost(entity: PostEntity) {
        try {
            val response = networkDataSource.createPost(entity.toPost())
            if (response.isSuccessful) {
                entity.isCreatedPostSynced = true
                entity.latestUpdate = System.currentTimeMillis()
                localDataSource.upsert(
                    entity
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun syncQueuedPosts() {
        localDataSource.getNotSyncedPosts()
            .forEach { entity ->
                syncCreatePost(entity)
            }
    }

    override suspend fun editPost(post: Post) {
        val postEntity = PostEntity(
            id = post.id ?: 0,
            body = post.body ?: "",
            title = post.title ?: "",
            userId = post.userId ?: 0,
            isCreatedPostSynced = false,
            latestUpdate = System.currentTimeMillis()
        )

        localDataSource.upsert(postEntity)
        syncEditPost(postEntity)
    }

    override suspend fun syncQueuedEditedPosts() {
        localDataSource.getNotSyncedEditedPosts()
            .forEach { entity ->
                syncEditPost(entity)
            }
    }

    private suspend fun syncEditPost(entity: PostEntity) {
        try {
            val response = networkDataSource.editPost(entity.id)
            if (response.isSuccessful) {
                entity.isEditedPostSynced = true
                entity.latestUpdate = System.currentTimeMillis()
                localDataSource.upsert(
                    entity
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deletePost(postId: Int) {
        val currentTime = System.currentTimeMillis()
        localDataSource.markAsDeleted(postId, currentTime)
    }

    override suspend fun syncQueuedDeletedPosts() {
        val deletedPosts = localDataSource.getDeletedPosts()
        for (post in deletedPosts) {
            try {
                networkDataSource.deletePost(post.id)
                localDataSource.deleteById(post.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}