package de.minmon.app.ui.screens.post

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.minmon.data.model.WordPressEvent
import de.minmon.data.model.WordPressPost
import de.minmon.data.repository.WordPressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: WordPressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PostScreenUiState())

    val state: StateFlow<PostScreenUiState>
        get() = _state

    fun loadPost(postId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            repository.getPost(postId).fold(
                onSuccess = { post ->
                    _state.value = _state.value.copy(
                        content = ContentType.Post(post),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load post"
                    )
                }
            )
        }
    }

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            repository.getEvent(eventId).fold(
                onSuccess = { event ->
                    _state.value = _state.value.copy(
                        content = ContentType.Event(event),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load event"
                    )
                }
            )
        }
    }

    fun onPostAction(action: PostAction) {
        when (action) {
            is PostAction.LoadPost -> loadPost(action.postId)
            is PostAction.LoadEvent -> loadEvent(action.eventId)
        }
    }
}

@Immutable
sealed interface PostAction {
    data class LoadPost(val postId: Int) : PostAction
    data class LoadEvent(val eventId: Int) : PostAction
}

sealed class ContentType {
    data class Post(val post: WordPressPost) : ContentType()
    data class Event(val event: WordPressEvent) : ContentType()
}

@Immutable
data class PostScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val content: ContentType? = null
)
