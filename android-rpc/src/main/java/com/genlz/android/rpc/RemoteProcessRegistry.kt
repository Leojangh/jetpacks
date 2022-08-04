package com.genlz.android.rpc

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.annotation.IntDef
import androidx.collection.ArrayMap
import androidx.core.os.bundleOf

/**
 * Singleton can't across processes.
 */
class RemoteProcessRegistry private constructor() {

    internal val functions = ArrayMap<Int, IRemoteProcess<out Parcelable>>()

    fun register(f: IRemoteProcess<out Parcelable>) {
        functions[f.id] = f
    }

    init {
        register(testMethod)
    }

    companion object {

        private const val TAG = "RemoteProcessRegistry"

        @Volatile
        @JvmStatic
        private var INSTANCE: RemoteProcessRegistry? = null

        @JvmStatic
        fun getInstance(): RemoteProcessRegistry {

            if (INSTANCE == null) {
                synchronized(RemoteProcessRegistry::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = RemoteProcessRegistry()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}

val testMethod = object : IRemoteProcess<Bundle> {

    override fun invoke(arg1: Int, arg2: Int, obj: Parcelable?): Bundle {
        throw RuntimeException("test")
        Log.d("RemoteProcessRegistry", "invoke: ......${Thread.currentThread()}")
        return bundleOf("result" to "Hello form server side.")
    }

    override val id: Int = 1
}
