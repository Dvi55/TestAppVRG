package com.example.testappvrg.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.testappvrg.R
import com.example.testappvrg.listener.ImageSaveListener
import com.example.testappvrg.retrofit.model.ChildData

internal class RedditPostAdapter(
    private val posts: MutableList<ChildData>,
    private val imageSaveListener: ImageSaveListener
) :
    RecyclerView.Adapter<RedditPostAdapter.PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.authorTextView.text = post.subreddit
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        val postTimeSeconds = post.createdUtc.toLong()
        val hoursAgo = (currentTimeSeconds - postTimeSeconds) / 3600
        holder.dateTextView.text = holder.itemView.context.getString(R.string.x_hours_ago, hoursAgo)
        holder.numCommentsTextView.text =
            holder.itemView.context.getString(R.string.num_comments, post.numComments)

        if (post.thumbnail.isNotEmpty() && post.thumbnail.endsWith("jpg")) {
            holder.thumbnailImageView.visibility = View.VISIBLE
            Glide.with(holder.thumbnailImageView.context)
                .load(post.thumbnail)
                .apply(RequestOptions().override(800, 600).downsample(DownsampleStrategy.AT_LEAST))
                .into(holder.thumbnailImageView)

            holder.thumbnailImageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.thumbnail))
                holder.thumbnailImageView.context.startActivity(intent)

                holder.thumbnailImageView.setOnLongClickListener {
                    imageSaveListener.onSaveImageToGallery(
                        holder.thumbnailImageView.context,
                        post.thumbnail
                    )
                    true
                }

            }
        } else {
            holder.thumbnailImageView.visibility = View.GONE
        }
        println("Post size is ${posts.size}")
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(newPosts: List<ChildData>) {
        val uniqueNewPosts = newPosts.filter { newPost ->
            !posts.any { existingPost ->
                existingPost.name == newPost.name
            }
        }
        val startPosition = posts.size
        posts.addAll(uniqueNewPosts)
        notifyItemRangeInserted(startPosition, uniqueNewPosts.size)
    }

    internal class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorTextView: TextView = itemView.findViewById(R.id.textAuthor)
        val dateTextView: TextView = itemView.findViewById(R.id.textDate)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.imageThumbnail)
        val numCommentsTextView: TextView = itemView.findViewById(R.id.textNumComments)
    }
}