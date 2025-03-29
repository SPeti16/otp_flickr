package com.test.otp.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.test.otp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FlickrServiceIModule {
    @Provides
    @Singleton
    fun provideFlickrServiceI(@ApplicationContext context: Context): FlickrServiceI {
        return FlickrService(context)
    }
}

interface FlickrServiceI {
    val flickrRepository: FlickrRepository
}

class FlickrService @Inject constructor(context: Context) : FlickrServiceI {
    private val baseUrl = context.getString(R.string.url_base)
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(context.getString(R.string.converter_factory).toMediaType()))
        .baseUrl(baseUrl)
        .build()


    private val retrofitService: FlickrApiService by lazy {
        retrofit.create(FlickrApiService::class.java)
    }

    override val flickrRepository: FlickrRepository by lazy {
        NetworkFlickrRepository(retrofitService)
    }
}