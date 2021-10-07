package com.genlz.jetpacks.utility

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

val Context.appCompatActivity
    get() = generateSequence(this) {
        if (it is ContextWrapper) {
            it.baseContext
        } else null
    }.firstOrNull { it is AppCompatActivity } as AppCompatActivity?
