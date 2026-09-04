package de.minmon.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordPressPost(
    @Json(name = "id")
    val id: Int,

    @Json(name = "date")
    val date: String,

    @Json(name = "date_gmt")
    val dateGmt: String,

    @Json(name = "modified")
    val modified: String,

    @Json(name = "slug")
    val slug: String,

    @Json(name = "status")
    val status: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "link")
    val link: String,

    @Json(name = "title")
    val title: RenderedContent,

    @Json(name = "content")
    val content: RenderedContent,

    @Json(name = "excerpt")
    val excerpt: RenderedContent,

    @Json(name = "author")
    val author: Int,

    @Json(name = "featured_media")
    val featuredMedia: Int,

    @Json(name = "categories")
    val categories: List<Int>,

    @Json(name = "tags")
    val tags: List<Int>,

    @Json(name = "_embedded")
    val embedded: Embedded? = null
)

@JsonClass(generateAdapter = true)
data class RenderedContent(
    @Json(name = "rendered")
    val rendered: String,

    @Json(name = "protected")
    val protected: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class Embedded(
    @Json(name = "author")
    val author: List<WordPressUser>? = null,

    @Json(name = "wp:featuredmedia")
    val featuredMedia: List<WordPressMedia>? = null,

    @Json(name = "wp:term")
    val terms: List<List<WordPressTerm>>? = null
)
