package com.example.posts.ui.posts.createPost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.posts.data.model.Post
import com.example.posts.databinding.CreatePostFragmentBinding
import com.example.posts.ui.posts.CreatePostState
import com.example.posts.ui.posts.PostsViewModel
import kotlinx.coroutines.launch
import kotlin.getValue


class CreatePostFragment : Fragment() {

    private val viewModel: PostsViewModel by activityViewModels()

    private var _binding: CreatePostFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreatePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            btnCreatePost.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val body = etBody.text.toString().trim()
                if (title.isEmpty() || body.isEmpty()) {
                    return@setOnClickListener
                }
                viewModel.createPost(
                    Post(
                        title = title,
                        body = body,
                        userId = 1
                    )
                )
            }
        }

        collectState()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createPostState.collect { state ->
                    if (state !is CreatePostState.Loading) {
                        binding.progressBar.visibility = View.GONE
                    }

                    when (state) {
                        is CreatePostState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is CreatePostState.Success -> {
                            viewModel.resetCreatePostState()
                            findNavController().navigateUp()
                        }

                        is CreatePostState.Initial -> {}

                        is CreatePostState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}