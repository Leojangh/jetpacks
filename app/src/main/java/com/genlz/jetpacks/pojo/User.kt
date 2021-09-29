package com.genlz.jetpacks.pojo

data class User(
    val uid: String,
    val avatar: String,
    val username: String,
    val identified: Boolean,
    val postDevice: String,
    val level: Int,
    val additional: String,
)
