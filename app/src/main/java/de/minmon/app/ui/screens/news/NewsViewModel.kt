package de.minmon.app.ui.screens.news

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.minmon.app.data.ThemeMode
import de.minmon.data.model.WordPressPost
import de.minmon.data.repository.WordPressRepository
import de.minmon.design.utils.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: WordPressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsScreenUiState())

    val state: StateFlow<NewsScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            loadPosts()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            repository.getPosts(perPage = 5).fold(
                onSuccess = { posts ->
                    _state.value = _state.value.copy(
                        posts = posts,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load posts"
                    )
                }
            )
        }
    }

    fun onNewsAction(action: NewsAction) {

    }

}

@Immutable
sealed interface NewsAction {

}

@Immutable
data class NewsScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val posts: List<WordPressPost> = emptyList()
)
