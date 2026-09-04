package de.galonga.audiotoolbox.app.di.koin

import de.galonga.audiotoolbox.app.settings.SettingsRepository
import de.galonga.audiotoolbox.app.ui.screens.analyzer.AnalyzerViewModel
import de.galonga.audiotoolbox.app.ui.screens.generator.SignalGeneratorViewModel
import de.galonga.audiotoolbox.app.ui.screens.record.RecordViewModel
import de.galonga.audiotoolbox.app.ui.screens.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

inline fun <reified T : Any> getKoinInstance(): T = object : KoinComponent {
    val value: T by inject()
}.value

val appModule = module {
    single { SettingsRepository(androidContext()) }
}

val viewModelModule = module {
    viewModelOf(::RecordViewModel)
    viewModelOf(::AnalyzerViewModel)
    viewModelOf(::SignalGeneratorViewModel)
    viewModelOf(::SettingsViewModel)
}
