package com.example.testappvrg.retrofit.api

import com.example.testappvrg.retrofit.model.RedditPost
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MainApi {
    @GET("top.json")
    suspend fun getTopPosts(
        @Query("after") after: String?,
        @Query("limit") limit: Int
    ): RedditPost
}