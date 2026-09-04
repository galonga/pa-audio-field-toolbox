package de.minmon.app.ui.screens.dates


import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.minmon.data.model.WordPressEvent
import de.minmon.data.repository.WordPressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DatesViewModel(
    private val repository: WordPressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DatesScreenUiState())

    val state: StateFlow<DatesScreenUiState>
        get() = _state

    init {
        loadDates()
    }

    fun onDatesAction(action: DatesAction) {
        when (action) {
            is DatesAction.Refresh -> loadDates()
        }
    }

    private fun loadDates() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            // Load events (wpkoi-events custom post type)
            repository.getEvents(perPage = 20).fold(
                onSuccess = { events ->
                    _state.value = _state.value.copy(
                        events = events,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load dates"
                    )
                }
            )
        }
    }
}

@Immutable
sealed interface DatesAction {
    object Refresh : DatesAction
}

@Immutable
data class DatesScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val events: List<WordPressEvent> = emptyList()
)
