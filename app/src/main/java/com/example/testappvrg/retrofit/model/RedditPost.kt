package com.example.testappvrg.retrofit.model

import com.google.gson.annotations.SerializedName


internal data class RedditPost(
    val data: Data
)

internal data class Data(
    val children: List<Children>
)

internal data class Children(
    val data: ChildData
)

internal data class ChildData(
    val subreddit: String,
    val selftext: String?,
    val title: String,
    val thumbnail: String,
    @SerializedName("num_comments") val numComments: Int,
    @SerializedName("created_utc") val createdUtc: Int,
    val name: String,
)