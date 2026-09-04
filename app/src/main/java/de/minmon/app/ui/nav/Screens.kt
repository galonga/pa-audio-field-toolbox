package de.minmon.app.ui.nav

sealed class Screens(open val route: String) {
    data class Post(val postId: Int, val isEvent: Boolean = false) : Screens("Post/$postId/${if (isEvent) "event" else "post"}")
}
