package de.minmon.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordPressUser(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "url")
    val url: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "link")
    val link: String,

    @Json(name = "slug")
    val slug: String,

    @Json(name = "avatar_urls")
    val avatarUrls: Map<String, String>? = null
)
