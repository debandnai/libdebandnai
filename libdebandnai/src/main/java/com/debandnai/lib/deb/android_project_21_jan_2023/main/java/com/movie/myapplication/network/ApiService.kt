package com.movie.myapplication.network

import com.movie.myapplication.data.model.popularMovie.popularMolieList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("trending/movie/day?")
    suspend fun getPopularMovieList(@Query("api_key") api_key:String): Response<popularMolieList>


    @GET("movie/top_rated?")
    suspend fun getTopRatedMovieList(@Query("api_key") api_key:String): Response<popularMolieList>
}