package com.example.ttmrcan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkChangeReceiver : BroadcastReceiver() {

    private var networkChangeListener: NetworkChangeListener? = null

    interface NetworkChangeListener {
        fun onNetworkAvailable()
        fun onNetworkUnavailable()
    }

    fun init(context: Context) {
        context.registerReceiver(
            this,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    fun release(context: Context) {
        context.unregisterReceiver(this)
    }

    fun setOnNetworkChangeListener(listener: NetworkChangeListener) {
        networkChangeListener = listener
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            // La conexión a Internet está disponible
            networkChangeListener?.onNetworkAvailable()
        } else {
            // No hay conexión a Internet
            networkChangeListener?.onNetworkUnavailable()
        }
    }
}