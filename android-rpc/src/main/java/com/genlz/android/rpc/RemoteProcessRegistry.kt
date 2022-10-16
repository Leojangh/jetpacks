package com.genlz.android.rpc

import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Singleton can't across processes.
 */
class RemoteProcessRegistry private constructor() {

    internal val functions: ConcurrentMap<Int, IRemoteProcess<*>> = ConcurrentHashMap()

    internal val binders = ConcurrentHashMap<String, IBinder>()

    fun register(f: IRemoteProcess<*>) {
        functions[f.id] = f
    }

    init {
        register(testMethod)
        binders["testBinder"] = TestBinder()
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

val testMethod = object : IRemoteProcess<List<String>> {

    override fun invoke(arg1: Int, arg2: Int, obj: Parcelable?, data: Bundle?): List<String> {
        return listOf("Hello")
    }

    override val id: Int = 1
}

class TestBinder : Binder() {

    fun test() = listOf("Test")
}
