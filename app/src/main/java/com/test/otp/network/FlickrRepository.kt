package com.test.otp.network

interface FlickrRepository {
    suspend fun getData(api: String, search: String, page: Int): FlickrApiData
    suspend fun getInfo(api: String, id: String): FlickrApiInfo
}

class NetworkFlickrRepository(
    private val flickrApiService: FlickrApiService
) : FlickrRepository {
    override suspend fun getData(api: String, search: String, page: Int): FlickrApiData = flickrApiService.searchPhotos(apiKey = api, text = search, page = page)
    override suspend fun getInfo(api: String, id: String): FlickrApiInfo = flickrApiService.getInfo(apiKey = api, id = id)
}