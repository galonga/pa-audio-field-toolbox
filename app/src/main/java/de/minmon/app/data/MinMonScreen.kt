package de.minmon.app.data

import androidx.annotation.StringRes
import de.minmon.app.R

enum class MinMonScreen (@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Dates(title = R.string.choose_dates),
    Podcast(title = R.string.choose_podcast),
    News(title = R.string.choose_podcast),
    Other(title = R.string.order_other),
    Record(title = R.string.record_screen),
    Post(title = R.string.app_name)
}