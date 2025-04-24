package com.rehman.template.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log

object NetworkUtils {
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private const val TAG = "NetworkUtils"

    fun registerNetworkCallback(context: Context, onNetworkStatusChanged: (Boolean) -> Unit) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network Status: Internet Connected.")
                onNetworkStatusChanged(true) // Internet Connected
            }

            override fun onLost(network: Network) {
                Log.e(TAG, "Network Status: No Internet Connection.")
                onNetworkStatusChanged(false) // Internet Disconnected
            }
        }

        val request = NetworkRequest.Builder().build()
        connectivityManager?.registerNetworkCallback(request, networkCallback!!)
    }

    fun unregisterNetworkCallback() {
        networkCallback?.let {
            connectivityManager?.unregisterNetworkCallback(it)
        }
    }
}
