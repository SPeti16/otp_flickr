package com.test.otp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApiService {
    @GET("services/rest/")
    suspend fun searchPhotos(
        @Query("method") method: String = "flickr.photos.search",
        @Query("api_key") apiKey: String,
        @Query("text") text: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("per_page") per: Int = 20,
        @Query("page") page: Int
    ): FlickrApiData

    @GET("services/rest/")
    suspend fun getInfo(
        @Query("method") method: String = "flickr.photos.getInfo",
        @Query("api_key") apiKey: String,
        @Query("photo_id") id: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1
    ): FlickrApiInfo
}