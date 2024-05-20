package com.example.testappvrg.di

import com.example.testappvrg.MainViewModel
import com.example.testappvrg.retrofit.api.MainApi
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .readTimeout(30_000, TimeUnit.MILLISECONDS)
            .writeTimeout(30_000, TimeUnit.MILLISECONDS)
            .callTimeout(30_000, TimeUnit.MILLISECONDS)
            .connectTimeout(30_000, TimeUnit.MILLISECONDS)
            .build()

    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://reddit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<MainApi> { get<Retrofit>().create() }
}
val appViewModelModule = module {
    viewModel { MainViewModel(get<MainApi>()) }
}