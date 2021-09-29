package com.genlz.jetpacks.data

import com.genlz.jetpacks.pojo.Post
import com.google.gson.annotations.SerializedName

data class RecommendResponse(
    val id: Int,
    val currentPage: Int,
    val totalPages: Int,
    @field:SerializedName("posts") val posts: List<Post>,
)
