package com.genlz.android.rpc

import android.os.Bundle
import android.os.Parcelable
import android.os.RemoteException
import java.io.Serializable

/**
 * The definition of remote method.
 * @param R The result type.Must be derived from [Parcelable] or [Serializable]
 */
interface IRemoteProcess<out R> {

    val id: Int

    @Throws(RemoteException::class)
    operator fun invoke(arg1: Int, arg2: Int, obj: Parcelable?, data: Bundle?): R
}