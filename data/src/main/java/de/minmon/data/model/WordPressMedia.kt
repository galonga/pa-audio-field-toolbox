package de.minmon.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordPressMedia(
    @Json(name = "id")
    val id: Int,

    @Json(name = "date")
    val date: String,

    @Json(name = "slug")
    val slug: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "link")
    val link: String,

    @Json(name = "title")
    val title: RenderedContent,

    @Json(name = "author")
    val author: Int,

    @Json(name = "caption")
    val caption: RenderedContent,

    @Json(name = "alt_text")
    val altText: String,

    @Json(name = "media_type")
    val mediaType: String,

    @Json(name = "mime_type")
    val mimeType: String,

    @Json(name = "media_details")
    val mediaDetails: MediaDetails,

    @Json(name = "source_url")
    val sourceUrl: String
)

@JsonClass(generateAdapter = true)
data class MediaDetails(
    @Json(name = "width")
    val width: Int,

    @Json(name = "height")
    val height: Int,

    @Json(name = "file")
    val file: String,

    @Json(name = "sizes")
    val sizes: Map<String, MediaSize>? = null
)

@JsonClass(generateAdapter = true)
data class MediaSize(
    @Json(name = "file")
    val file: String,

    @Json(name = "width")
    val width: Int,

    @Json(name = "height")
    val height: Int,

    @Json(name = "mime_type")
    val mimeType: String,

    @Json(name = "source_url")
    val sourceUrl: String
)
