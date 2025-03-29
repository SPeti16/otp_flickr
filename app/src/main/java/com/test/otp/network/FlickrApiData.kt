package com.test.otp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlickrApiData(
    val photos: PhotoList,
    val stat: String
)

@Serializable
data class PhotoList(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<Photo>
)

@Serializable
data class Photo(
    val id: String,
    val server: String,
    val secret: String,
    val farm: Int,
    val title: String
) {
    fun getImageUrl(): String {
        return "https://farm$farm.staticflickr.com/$server/${id}_$secret.jpg"
    }
}

@Serializable
data class FlickrApiInfo(
    val photo: Info,
    val stat: String
)

@Serializable
data class Info(
    val id: String,
    val server: String,
    val secret: String,
    val farm: Int,
    val title: Content,
    val description: Content,
    val owner: Owner,
    val dates: Dates
) {
    fun getImageUrl(): String {
        return "https://farm$farm.staticflickr.com/$server/${id}_$secret.jpg"
    }
}

@Serializable
data class Content(
    @SerialName("_content")
    val content: String
)

@Serializable
data class Owner(
    val username: String
)

@Serializable
data class Dates(
    val posted: String,
    val taken: String,
    val lastupdate: String
)