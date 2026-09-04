package de.minmon.data.api

import de.minmon.data.model.WordPressCategory
import de.minmon.data.model.WordPressEvent
import de.minmon.data.model.WordPressMedia
import de.minmon.data.model.WordPressPost
import de.minmon.data.model.WordPressUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordPressApiService {

    @GET("post")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("_embed") embed: Boolean = true,
        @Query("categories") categories: String? = null,
        @Query("tags") tags: String? = null,
        @Query("search") search: String? = null
    ): List<WordPressPost>

    @GET("wpkoi-events")
    suspend fun getEvents(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("_embed") embed: Boolean = true,
        @Query("categories") categories: String? = null,
        @Query("tags") tags: String? = null,
        @Query("search") search: String? = null
    ): List<WordPressEvent>

    @GET("post/{id}")
    suspend fun getPost(
        @Path("id") id: Int,
        @Query("_embed") embed: Boolean = true
    ): WordPressPost

    @GET("wpkoi-events/{id}")
    suspend fun getEvent(
        @Path("id") id: Int,
        @Query("_embed") embed: Boolean = true
    ): WordPressEvent

    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): List<WordPressCategory>

    @GET("categories/{id}")
    suspend fun getCategory(
        @Path("id") id: Int
    ): WordPressCategory

    @GET("tags")
    suspend fun getTags(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): List<WordPressCategory>

    @GET("media/{id}")
    suspend fun getMedia(
        @Path("id") id: Int
    ): WordPressMedia

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): WordPressUser
}
