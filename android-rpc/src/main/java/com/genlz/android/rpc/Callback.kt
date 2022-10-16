package com.genlz.android.rpc

fun interface Callback<in R> {

    fun onSuccess(result: R)

    fun onException(e: Exception) {
        throw RuntimeException(e)
    }
}