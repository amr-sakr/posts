package com.example.posts.ui.posts.postsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.posts.R
import com.example.posts.databinding.PostsFragmentBinding
import com.example.posts.ui.posts.DeletePostState
import com.example.posts.ui.posts.PostsState
import com.example.posts.ui.posts.PostsViewModel
import com.example.posts.ui.posts.editPost.EditPostFragment.Companion.KEY_POST_EDITED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFragment : Fragment() {

    private var _binding: PostsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by activityViewModels()


    private val postsAdapter: PostsAdapter by lazy {
        PostsAdapter(onEditListener = { post ->
            findNavController().navigate(R.id.editPostFragment, bundleOf("post" to post))
        }, onDeleteListener = { id ->
            viewModel.deletePost(id)
            collectPosts()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PostsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPosts.adapter = postsAdapter

        binding.btnAddPost.setOnClickListener {
            findNavController().navigate(R.id.createPostBottomSheet)
        }
        collectPosts()
        collectDeletePostState()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            KEY_POST_EDITED
        )
            ?.observe(viewLifecycleOwner) {
                collectPosts()
            }
    }

    private fun collectPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postsList.collect { state ->

                    if (state !is PostsState.Loading) {
                        binding.progressBar.visibility = View.GONE
                    }

                    when (state) {
                        is PostsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is PostsState.Success -> {
                            postsAdapter.submitList(state.posts)
                            binding.tvNoPosts.visibility = View.GONE
                            binding.rvPosts.visibility = View.VISIBLE
                        }

                        is PostsState.Empty -> {
                            binding.tvNoPosts.visibility = View.VISIBLE
                            binding.rvPosts.visibility = View.GONE
                        }

                        is PostsState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun collectDeletePostState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deletePostState.collect { state ->

                    if (state !is DeletePostState.Loading) {
                        binding.progressBar.visibility = View.GONE
                    }
                    when (state) {
                        is DeletePostState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is DeletePostState.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "Post deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetDeletePostState()
                        }

                        is DeletePostState.Initial -> {}

                        is DeletePostState.Error -> {
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
        binding.rvPosts.adapter = null
        _binding = null
    }
}