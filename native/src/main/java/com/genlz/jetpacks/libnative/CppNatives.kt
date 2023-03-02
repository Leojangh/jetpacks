package com.genlz.jetpacks.libnative

class CppNatives {
    companion object {
        init {
            System.loadLibrary("jetpacks")
        }

        @JvmStatic
        external fun androidJni(): Int
    }
}