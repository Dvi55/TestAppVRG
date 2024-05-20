package com.example.testappvrg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testappvrg.retrofit.api.MainApi
import com.example.testappvrg.retrofit.model.ChildData
import kotlinx.coroutines.launch

internal class MainViewModel(private val api: MainApi) : ViewModel() {

    private val _posts = MutableLiveData<List<ChildData>>()
    val posts: LiveData<List<ChildData>> get() = _posts

    private var after: String? = null

    private var isLoading = false
    fun loadMorePosts() {
        if (isLoading) return

        isLoading = true
        viewModelScope.launch {
            try {
                val response = api.getTopPosts(after, 5)
                val newPosts = response.data.children.map { it.data }
                after = newPosts.lastOrNull()?.name
                println("Doing request with after: $after")
                val currentPosts = _posts.value.orEmpty().toMutableList()
                currentPosts.addAll(newPosts)
                _posts.postValue(currentPosts)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}