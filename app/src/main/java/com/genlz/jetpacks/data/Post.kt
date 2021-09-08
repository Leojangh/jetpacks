package com.genlz.jetpacks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Domain pojo cross through application.
 */
data class Post(
    val id: Long,
    val title: String,
    val abstraction: String,
    val thumbnails: List<String>,
    val tags: List<String>,
    val user: User,
    val postTime: LocalDateTime,
    val views: Int,
    val comments: Int,
    val thumbs: Int,
)

/**
 * Dedicate into network domain,such as serializing to JSON or inverse.
 */
data class NetworkPostEntity(
    val id: Long,
    val title: String,
    val abstraction: String,
    val thumbnails: List<String>,
    val tags: List<String>,
    val user: User,
    val postTime: LocalDateTime,
    val views: Int,
    val comments: Int,
    val thumbs: Int,
)

/**
 * Dedicate into persistence domain,such as representing RECORD.
 * TODO
 */
@Entity(tableName = "post")
data class PersistencePostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
)

