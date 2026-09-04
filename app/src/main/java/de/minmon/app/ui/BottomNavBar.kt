package de.minmon.app.ui


import de.minmon.app.R
import de.minmon.app.data.MinMonScreen

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){
    object Home : BottomNavItem(MinMonScreen.Home.name, R.drawable.ic_news, MinMonScreen.Home.name)
    object Podcast: BottomNavItem(MinMonScreen.Podcast.name,R.drawable.ic_podcast,MinMonScreen.Podcast.name)
    object Dates: BottomNavItem(MinMonScreen.Dates.name,R.drawable.ic_dates,MinMonScreen.Dates.name)
    object Other: BottomNavItem(MinMonScreen.Other.name,R.drawable.ic_more,MinMonScreen.Other.name)
    object Record: BottomNavItem(MinMonScreen.Record.name,R.drawable.ic_record,MinMonScreen.Record.name)
}