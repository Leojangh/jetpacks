package com.genlz.libnative

import android.os.Build

actual fun functionInNative(): Int {
    return Build.VERSION.SDK_INT
}
