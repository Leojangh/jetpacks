package com.genlz.jetpacks.data

import com.google.gson.annotations.SerializedName

data class RecommendResponse(
    val id: Int,
    val postType: PostType,
    @field:SerializedName("post") val post: Post,
)
