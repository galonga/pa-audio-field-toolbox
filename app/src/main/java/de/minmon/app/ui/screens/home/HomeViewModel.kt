package de.minmon.app.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.minmon.app.data.ThemeMode
import de.minmon.app.ui.nav.Screens
import de.minmon.app.util.Router
import de.minmon.app.util.ThemeConfiguration
import de.minmon.app.util.ThemeConfigurator
import de.minmon.data.model.WordPressPost
import de.minmon.data.repository.WordPressRepository
import de.minmon.design.utils.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WordPressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenUiState())

    val state: StateFlow<HomeScreenUiState>
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

    fun onHomeAction(action: HomeAction) {
        when(action){
            is HomeAction.OnPostClick -> Router.dispatch(Router.NavigationType.NavigateTo(Screens.Post(action.postId)))
        }
    }
}

@Immutable
sealed interface HomeAction {
    data class OnPostClick(val postId: Int) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    var themeMode: Int = ThemeMode.AUTO,
    var themeDynColorMode: Boolean = false,
    val posts: List<WordPressPost> = emptyList()
)
