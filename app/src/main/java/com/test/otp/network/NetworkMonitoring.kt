package com.test.otp.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkMonitoringModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideNetworkHelper(context: Context): NetworkMonitoring {
        return NetworkMonitoring(context)
    }
}

class NetworkMonitoring(private val context: Context) {
    fun isConnectedToWifiOrMobileData(): NetworkCategory {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkCategory.NONE
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkCategory.NONE

        if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ){
            return NetworkCategory.WIFI
        }
        if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ){
            return NetworkCategory.CELLULAR
        }

        return NetworkCategory.NONE
    }
}

enum class NetworkCategory {
    WIFI,
    CELLULAR,
    NONE,
    LOADING
}