package com.genlz.jetpacks.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.genlz.jetpacks.data.PersistencePostEntity

@Database(entities = [PersistencePostEntity::class], version = 1)
@TypeConverters(Converter::class)
abstract class ThumbsDb : RoomDatabase() {

    abstract fun postDao(): PostDao

    companion object {
        const val DATABASE_NAME = "post_db"
    }
}