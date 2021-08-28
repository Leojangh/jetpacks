package com.genlz.jetpacks.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.genlz.jetpacks.data.PersistencePostEntity

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg post: PersistencePostEntity)

}
