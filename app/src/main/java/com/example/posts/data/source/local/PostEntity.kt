package com.example.posts.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "post"
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val body: String = "",
    val title: String = "",
    val userId: Int = 0,
    var isCreatedPostSynced: Boolean = false,
    var isEditedPostSynced: Boolean = false,
    var latestUpdate: Long = 0,
    var isDeleted: Boolean = false
)