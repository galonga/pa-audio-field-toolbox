package de.minmon.data.repository

import de.minmon.data.api.WordPressApiService
import de.minmon.data.model.WordPressCategory
import de.minmon.data.model.WordPressEvent
import de.minmon.data.model.WordPressPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordPressRepository(
    private val apiService: WordPressApiService
) {

    suspend fun getPosts(
        page: Int = 1,
        perPage: Int = 10,
        categories: String? = null,
        tags: String? = null,
        search: String? = null
    ): Result<List<WordPressPost>> = withContext(Dispatchers.IO) {
        try {
            val posts = apiService.getPosts(
                page = page,
                perPage = perPage,
                embed = true,
                categories = categories,
                tags = tags,
                search = search
            )
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPost(id: Int): Result<WordPressPost> = withContext(Dispatchers.IO) {
        try {
            val post = apiService.getPost(id, embed = true)
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getEvents(
        page: Int = 1,
        perPage: Int = 10,
        categories: String? = null,
        tags: String? = null,
        search: String? = null
    ): Result<List<WordPressEvent>> = withContext(Dispatchers.IO) {
        try {
            val events = apiService.getEvents(
                page = page,
                perPage = perPage,
                embed = true,
                categories = categories,
                tags = tags,
                search = search
            )
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEvent(id: Int): Result<WordPressEvent> = withContext(Dispatchers.IO) {
        try {
            val event = apiService.getEvent(id, embed = true)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(
        page: Int = 1,
        perPage: Int = 100
    ): Result<List<WordPressCategory>> = withContext(Dispatchers.IO) {
        try {
            val categories = apiService.getCategories(page, perPage)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategory(id: Int): Result<WordPressCategory> = withContext(Dispatchers.IO) {
        try {
            val category = apiService.getCategory(id)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTags(
        page: Int = 1,
        perPage: Int = 100
    ): Result<List<WordPressCategory>> = withContext(Dispatchers.IO) {
        try {
            val tags = apiService.getTags(page, perPage)
            Result.success(tags)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
