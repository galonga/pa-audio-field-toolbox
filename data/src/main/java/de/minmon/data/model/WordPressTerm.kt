package de.minmon.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordPressTerm(
    @Json(name = "id")
    val id: Int,

    @Json(name = "link")
    val link: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "slug")
    val slug: String,

    @Json(name = "taxonomy")
    val taxonomy: String
)
