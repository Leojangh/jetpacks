package com.genlz.jetpacks.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log

/**
 * AIDL sample.
 *
 * * Create AIDL with java language syntax.
 *
 * * Build AIDL to generate source code.
 *
 * * Implement generated Stub and return it in method [Service.onBind].
 *
 * * Received binder mentioned last step,transform to interface using
 * [IRemoteService.Stub.asInterface] in method [android.content.ServiceConnection.onServiceConnected]
 *
 */
class RemoteService : Service() {

    override fun onBind(intent: Intent?): IBinder = object : IRemoteService.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?,
        ) {
            Log.d(TAG, "basicTypes: ${Process.myPid()}")
        }
    }

    companion object {
        private const val TAG = "RemoteService"
    }
}