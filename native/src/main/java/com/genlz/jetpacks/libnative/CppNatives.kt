package com.genlz.jetpacks.libnative

class CppNatives {
    companion object {
        init {
            System.loadLibrary("jetpacks")
        }

        @JvmStatic
        external fun androidJni(): Int

        @JvmStatic
        external fun setAffinity(tid: Int = 0)

        @JvmStatic external fun whereAmIRunning():Int
    }
}