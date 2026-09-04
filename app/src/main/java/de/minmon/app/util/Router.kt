package de.minmon.app.util

import androidx.annotation.IdRes
import de.minmon.app.ui.nav.Screens
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object Router {

    private val _sharedFlow =
        MutableSharedFlow<NavigationType>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun dispatch(navTarget: NavigationType) {
        _sharedFlow.tryEmit(navTarget)
    }

    sealed class NavigationType {
        data class NavigateTo(val target: Screens) : NavigationType()
        data object PopBack : NavigationType()
        data class PopUpTo(@IdRes val destinationId: Int, val inclusive: Boolean) : NavigationType()
    }
}
