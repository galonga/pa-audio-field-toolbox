package de.minmon.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordPressEvent(
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

    @Json(name = "featured_media")
    val featuredMedia: Int,

    @Json(name = "comment_status")
    val commentStatus: String? = null,

    @Json(name = "ping_status")
    val pingStatus: String? = null,

    @Json(name = "_embedded")
    val embedded: EventEmbedded? = null
)

@JsonClass(generateAdapter = true)
data class EventEmbedded(
    @Json(name = "wp:featuredmedia")
    val featuredMedia: List<WordPressMedia>? = null
)
