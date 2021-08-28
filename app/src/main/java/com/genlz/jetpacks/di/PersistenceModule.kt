package com.genlz.jetpacks.di

import android.content.Context
import androidx.room.Room
import com.genlz.jetpacks.persistence.Converter
import com.genlz.jetpacks.persistence.PostDao
import com.genlz.jetpacks.persistence.ThumbsDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    fun providePostDao(
        thumbsDb: ThumbsDb,
    ): PostDao {
        return thumbsDb.postDao()
    }

    @Provides
    fun providePostDatabase(
        @ApplicationContext context: Context,
        converter: Converter,
    ): ThumbsDb {
        return Room.databaseBuilder(
            context,
            ThumbsDb::class.java,
            ThumbsDb.DATABASE_NAME
        ).addTypeConverter(converter)
            .build()
    }
}