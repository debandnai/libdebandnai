package com.movie.myapplication.network

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
        @Singleton
        @Provides
        fun getRetroInstance(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }

        @Singleton
        @Provides
        fun getClient(): Retrofit {

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                val client: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build()

                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                return Retrofit.Builder()
                    .baseUrl(Config.BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

        }
}