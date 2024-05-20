package com.example.testappvrg

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.testappvrg.adapter.RedditPostAdapter
import com.example.testappvrg.listener.ImageSaveListener
import org.koin.android.ext.android.inject
import java.io.OutputStream

internal class MainActivity : AppCompatActivity(), ImageSaveListener {

    private val viewModel: MainViewModel by inject()
    private lateinit var redditPostAdapter: RedditPostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        redditPostAdapter = RedditPostAdapter(mutableListOf(), this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = redditPostAdapter

        viewModel.posts.observe(this, Observer { posts ->
            redditPostAdapter.updatePosts(posts)
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findLastVisibleItemPosition() == redditPostAdapter.itemCount - 1) {
                    viewModel.loadMorePosts()
                    println("Loading more posts from main activity")
                }
            }
        })
        if (savedInstanceState == null) {
            viewModel.loadMorePosts()
        } else {
            val posts = viewModel.posts.value
            if (!posts.isNullOrEmpty()) {
                redditPostAdapter.updatePosts(posts)
            } else {
                viewModel.loadMorePosts()
            }
        }
    }


    override fun onSaveImageToGallery(context: Context, imageUrl: String) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val contentResolver = context.contentResolver
                    val imageCollection =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        } else {
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }

                    val contentValues = ContentValues().apply {
                        put(
                            MediaStore.Images.Media.DISPLAY_NAME,
                            "IMG_${System.currentTimeMillis()}.jpg"
                        )
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.WIDTH, resource.width)
                        put(MediaStore.Images.Media.HEIGHT, resource.height)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val imageUri: Uri? = contentResolver.insert(imageCollection, contentValues)
                    if (imageUri != null) {
                        saveBitmapToStream(resource, contentResolver.openOutputStream(imageUri))
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                            contentResolver.update(imageUri, contentValues, null, null)
                        }
                        Toast.makeText(context, "Saved to gallery", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun saveBitmapToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        outputStream.use { stream ->
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        }
    }
}