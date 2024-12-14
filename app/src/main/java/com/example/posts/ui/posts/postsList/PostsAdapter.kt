package com.example.posts.ui.posts.postsList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.posts.data.model.Post
import com.example.posts.databinding.PostsListItemBinding

class PostsAdapter(
    private val onEditListener: (Post) -> Unit,
    private val onDeleteListener: (Int) -> Unit,
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffUtil) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        return PostViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onEditListener, onDeleteListener)
    }

    companion object {
        object PostDiffUtil : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(
                oldItem: Post,
                newItem: Post
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Post,
                newItem: Post
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class PostViewHolder(
        private val binding: PostsListItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            post: Post,
            onEditListener: (Post) -> Unit,
            onDeleteListener: (Int) -> Unit
        ) {
            binding.apply {
                tvTitle.text = post.title
                tvBody.text = post.body
                ivEdit.setOnClickListener {
                    onEditListener(post)
                }

                ivDelete.setOnClickListener {
                    onDeleteListener(post.id ?: 0)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): PostViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PostsListItemBinding.inflate(layoutInflater, parent, false)
                return PostViewHolder(binding)

            }
        }

    }
}