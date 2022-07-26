package com.genlz.android.rpc

import android.os.Parcelable
import android.os.RemoteException

interface IRemoteProcess<R : Parcelable> {

    val id: Int

    @Throws(RemoteException::class)
    operator fun invoke(arg1: Int, arg2: Int, obj: Parcelable?): R?
}