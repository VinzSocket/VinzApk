package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<CollectionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CollectionItem)

    @Query("DELETE FROM collection WHERE originalUrl = :url")
    suspend fun deleteItemByUrl(url: String)
    
    @Query("SELECT * FROM collection WHERE originalUrl = :url LIMIT 1")
    suspend fun getItemByUrl(url: String): CollectionItem?
}
