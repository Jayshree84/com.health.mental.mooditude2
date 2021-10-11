package com.health.mental.mooditude.activity.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.health.mental.mooditude.adapter.CommunityPostAdapter
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.PostCategory
import com.health.mental.mooditude.databinding.FragmentPostCatDetailsBinding
import com.health.mental.mooditude.fragment.BaseFragment


class PostCatDetailsFragment(private val mPostCat: PostCategory) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentPostCatDetailsBinding

    companion object {
        fun newInstance(postCategory: PostCategory): PostCatDetailsFragment {
            val fragment = PostCatDetailsFragment(postCategory)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostCatDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }

    private fun createViews() {

        binding.listviewEntries.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                activity,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )

        val listPosts = DBManager.instance.getAllFeedPostsByCategory(mPostCat.categoryId)

        val adapter = CommunityPostAdapter(
            requireActivity(), null, listPosts,
            parentFragment as CommunityFragment,
            PostCatDetailsFragment::class.java.simpleName
        )
        binding.listviewEntries.adapter = adapter

        addScrollListener(binding.listviewEntries)
    }

    fun getCategoryTitle() = mPostCat.title

    fun getCategoryId() = mPostCat.categoryId
}
