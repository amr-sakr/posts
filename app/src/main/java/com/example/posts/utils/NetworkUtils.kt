package com.example.posts.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkUtils @Inject constructor() {

    @Inject
    lateinit var connectivityManager: ConnectivityManager
    fun isConnectedToNetwork(): Boolean {
        var result = false

        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> true
                else -> false
            }
        }

        return result
    }
}