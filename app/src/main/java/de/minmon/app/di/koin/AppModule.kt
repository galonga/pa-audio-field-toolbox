package de.minmon.app.di.koin

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.minmon.app.ui.screens.dates.DatesViewModel
import de.minmon.app.ui.screens.home.HomeViewModel
import de.minmon.app.ui.screens.other.OtherViewModel
import de.minmon.app.ui.screens.podcast.PodcastViewModel
import de.minmon.app.ui.screens.post.PostViewModel
import de.minmon.app.ui.screens.record.RecordViewModel
import de.minmon.data.api.WordPressApiService
import de.minmon.data.repository.WordPressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

inline fun <reified T : Any> getKoinInstance(): T = object : KoinComponent {
    val value: T by inject()
}.value

val appModule = module {
    // Moshi
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // OkHttp
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://www.minmon.de/wp-json/wp/v2/")
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    // API Service
    single {
        get<Retrofit>().create(WordPressApiService::class.java)
    }

    // Repository
    single {
        WordPressRepository(get())
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::PodcastViewModel)
    viewModelOf(::OtherViewModel)
    viewModelOf(::DatesViewModel)
    viewModelOf(::PostViewModel)
    viewModelOf(::RecordViewModel)
}

val coroutineScopeModule = module {
    single<CoroutineScope> { CoroutineScope(Dispatchers.IO) }
}
