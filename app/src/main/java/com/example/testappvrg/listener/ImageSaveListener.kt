package com.example.testappvrg.listener

import android.content.Context

interface ImageSaveListener {
    fun onSaveImageToGallery(context: Context, imageUrl: String)
}