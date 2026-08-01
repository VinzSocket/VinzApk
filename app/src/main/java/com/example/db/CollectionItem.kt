package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection")
data class CollectionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val streamUrl: String,
    val originalUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
