package com.example.posts.ui.posts.editPost

import android.os.Build
import android.os.Build.VERSION_CODES
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
import com.example.posts.databinding.EditPostFragmentBinding
import com.example.posts.ui.posts.EditPostState
import com.example.posts.ui.posts.PostsViewModel
import kotlinx.coroutines.launch


class EditPostFragment : Fragment() {

    companion object{
        const val KEY_POST_EDITED = "post_edited"
    }

    private var _binding: EditPostFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val post = arguments?.let {
            if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                it.getParcelable("post", Post::class.java)
            } else {
                it.getParcelable<Post>("post")
            }
        }
        viewModel.editPostArg = post
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditPostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.editPostArg?.let {
            binding.etTitle.setText(it.title)
            binding.etBody.setText(it.body)

        }

        binding.btnEditPost.setOnClickListener {
            viewModel.editPost(
                Post(
                    id = viewModel.editPostArg?.id,
                    userId = viewModel.editPostArg?.userId,
                    title = binding.etTitle.text.toString(),
                    body = binding.etBody.text.toString()
                )
            )
        }

        collectEditPostState()
    }

    private fun collectEditPostState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editPostState.collect { state ->
                    if (state !is EditPostState.Loading) {
                        binding.progressBar.visibility = View.GONE
                    }

                    when (state) {
                        is EditPostState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is EditPostState.Success -> {
                            Toast.makeText(requireContext(), "Post Edited Successfully", Toast.LENGTH_SHORT).show()
                            viewModel.resetEditPostState()
                            findNavController().previousBackStackEntry?.savedStateHandle?.set<Boolean>(
                                KEY_POST_EDITED,
                                true
                            )
                            findNavController().navigateUp()
                        }

                        is EditPostState.Initial -> {}

                        is EditPostState.Error -> {
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