package com.test.otp.db

import android.content.Context
import android.content.SharedPreferences
import com.test.otp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbClientModule {
    @Provides
    @Singleton
    fun provideDbClient(@ApplicationContext context: Context): DbClient {
        return DbClient(context, context.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE))
    }
}

class DbClient(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    fun getSearch(): String {
        val default = context.getString(R.string.default_search)
        return sharedPreferences.getString(context.getString(R.string.key_search), default)?:default
    }

    fun putSearch(value: String) {
        sharedPreferences.edit().putString(context.getString(R.string.key_search), value).apply()
    }
}