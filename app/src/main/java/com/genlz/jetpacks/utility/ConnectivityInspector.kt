package com.genlz.jetpacks.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.genlz.share.util.appcompat.getSystemService

class ConnectivityInspector(
    context: Context,
) : LiveData<Boolean>() {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    private val validNetworks = mutableSetOf<Network>()

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternet = networkCapabilities
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (hasInternet == true) {
                validNetworks += network
            }
            checkValidNetworks()
        }

        override fun onLost(network: Network) {
            validNetworks -= network
            checkValidNetworks()
        }
    }

    private fun checkValidNetworks() {
        postValue(validNetworks.isNotEmpty())
    }

    override fun onActive() {
        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
