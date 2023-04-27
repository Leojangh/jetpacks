package com.genlz.jetpacks.libnative

class CppNatives {
    companion object {
        init {
            System.loadLibrary("jetpacks")
        }

        @JvmStatic
        external fun androidJni(): Int

        /**
         * Set thread affinity for [tid],0 represents the current thread.
         *
         *
         */
        @JvmStatic
        external fun setAffinity(tid: Int = 0, cpus: IntArray): Int

        /**
         * Get which cpu is the current thread running on.
         */
        @JvmStatic
        external fun whereAmIRunning(): Int

        @JvmStatic
        external fun search(target: String, pattern: String): Boolean
    }
}