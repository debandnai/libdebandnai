package com.salonsolution.app.di

import com.salonsolution.app.data.network.*
import com.salonsolution.app.data.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit.Builder {
        return RetrofitHelper.getClient()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HeaderInterceptor, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).authenticator(tokenAuthenticator ).build()
    }

    @Singleton
    @Provides
    fun providesApiService(retrofitBuilder: Retrofit.Builder): ApiService {
        //without token
        return retrofitBuilder.build().create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesUserApi(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): UserApi {
        //with token
        return retrofitBuilder.client(okHttpClient).build().create(UserApi::class.java)
    }

    @Singleton
    @Provides
    fun providesRefreshTokenApiService(retrofitBuilder: Retrofit.Builder): TokenRefreshApi {
        //without token
        return retrofitBuilder.build().create(TokenRefreshApi::class.java)
    }

}