package com.example.testappvrg.retrofit.model


data class RedditPost(
    val data: Data
)

data class Data(
    val children: List<Children>
)

data class Children(
    val data: ChildData
)

data class ChildData(
    val subreddit: String,
    val selftext: String?,
    val title: String,
    val thumbnail: String,
    val numComments: Long,
    val createdUtc: Int,
)