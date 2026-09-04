package de.minmon.app.ui.screens.podcast


import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.minmon.data.model.WordPressPost
import de.minmon.data.repository.WordPressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PodcastViewModel(
    private val repository: WordPressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PodcastScreenUiState())

    val state: StateFlow<PodcastScreenUiState>
        get() = _state

    init {
        loadPodcasts()
    }

    fun onPodcastAction(action: PodcastAction) {
        when (action) {
            is PodcastAction.Refresh -> loadPodcasts()
        }
    }

    private fun loadPodcasts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            // Load posts with "podcast" category (ID: 7)
            repository.getPosts(perPage = 20, categories = "7").fold(
                onSuccess = { posts ->
                    _state.value = _state.value.copy(
                        posts = posts,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load podcasts"
                    )
                }
            )
        }
    }
}

@Immutable
sealed interface PodcastAction {
    object Refresh : PodcastAction
}

@Immutable
data class PodcastScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val posts: List<WordPressPost> = emptyList()
)
