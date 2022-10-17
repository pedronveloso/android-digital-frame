package com.pedronveloso.digitalframe.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherServiceApi
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherServiceImpl
import com.pedronveloso.digitalframe.network.serialization.LocalDateTimeDeserializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object OpenWeatherNetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    @Provides
    fun profileRetrofit(): Retrofit {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun profileRetrofitServiceApi(retrofit: Retrofit): OpenWeatherServiceApi {
        return retrofit.create(OpenWeatherServiceApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBinds {

    @Binds
    abstract fun bindApiService(
        apiServiceImpl: OpenWeatherServiceImpl
    ): OpenWeatherService
}
