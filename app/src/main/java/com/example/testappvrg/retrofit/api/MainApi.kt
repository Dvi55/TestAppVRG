package com.example.testappvrg.retrofit.api

import com.example.testappvrg.retrofit.model.RedditPost
import retrofit2.http.GET

interface MainApi {
    @GET("popular/top.json")
    suspend fun getTopPosts():RedditPost
}