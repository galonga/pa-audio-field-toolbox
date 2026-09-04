package de.galonga.audiotoolbox.app.startup

import android.content.Context
import androidx.startup.Initializer
import de.galonga.audiotoolbox.app.di.koin.appModule
import de.galonga.audiotoolbox.app.di.koin.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@Suppress("unused")
class KoinInitializer : Initializer<KoinApplication> {
    override fun create(context: Context): KoinApplication = startKoin {
        androidLogger(Level.ERROR)
        androidContext(context)
        modules(buildModuleList())
    }

    private fun buildModuleList() = buildList {
        addAll(
            listOf(
                appModule,
                viewModelModule
            )
        )
    }

    // No dependencies on other libraries.
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
