package com.genlz.jetpacks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Domain pojo cross through application.
 */
data class Post(
    val id: Long,
    val title: String,
    val abstraction: String,
    val thumbnail: String,
    val body: String,
)

fun Post.mapToNetworkPostEntity() = NetworkPostEntity(
    id,
    title,
    abstraction,
    thumbnail,
    body
)

fun Post.mapToPersistenceEntity() = PersistencePostEntity(
    id,
    title,
    abstraction,
    thumbnail,
    body,
    LocalDateTime.now(ZoneId.systemDefault())
)

/**
 * Dedicate into network domain,such as serializing to JSON or inverse.
 */
data class NetworkPostEntity(
    val id: Long,
    val title: String,
    val abstraction: String,
    val thumbnail: String,
    val body: String,
)

fun NetworkPostEntity.mapToPost() = Post(id, title, abstraction, thumbnail, body)

/**
 * Dedicate into persistence domain,such as representing RECORD.
 */
@Entity(tableName = "post")
data class PersistencePostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var title: String,
    var abstraction: String,
    var thumbnail: String,
    var body: String,
    var cachedTime: LocalDateTime,
)

fun PersistencePostEntity.mapToPost() = Post(id, title, abstraction, thumbnail, body)


