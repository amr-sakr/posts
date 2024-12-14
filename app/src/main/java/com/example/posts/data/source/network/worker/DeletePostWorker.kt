package com.example.posts.data.source.network.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.posts.data.IPostsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeletePostWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: IPostsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncQueuedDeletedPosts()
            Result.success()
        } catch (e: Exception) {
            Log.e("DeletePostWorker", "Error syncing posts: ${e.message}")
            Result.retry()
        }
    }
}