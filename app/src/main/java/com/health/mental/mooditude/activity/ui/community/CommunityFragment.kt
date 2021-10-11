package com.health.mental.mooditude.activity.ui.community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.data.entity.PostCategory
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.databinding.FragmentCommunityBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.*

class CommunityFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //add fragment
        val fragment = MainFragment()
        addFragment(R.id.layout_container, fragment, true)

        binding.fab.setOnClickListener {
            var currCategory = ""
            val fragment2 =
                childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName)
            debugLog(TAG, "Fragment2 found : " + fragment2)

            if (fragment2 != null) {
                currCategory = (fragment2 as MainFragment).getCategoryId()
            }

            val activity1 = requireActivity() as BaseActivity
            val intent = Intent(activity1, AddNewPostActivity::class.java)
            intent.putExtra("category", currCategory)
            activity1.startActivityForResult( REQUEST_ADD_NEW_POST, intent)
        }

        return root
    }

    fun onPostItemClicked(post: ApiPost, fragmentName: String) {
        //UiUtils.showSuccessToast(requireActivity(), "Item clicked : " + post.postId)
        val activity1 = requireActivity() as BaseActivity
        val intent = Intent(activity1, PostDetailsActivity::class.java)
        intent.putExtra("post", Gson().toJson(post))
        activity1.startActivityForResult(REQUEST_POST_DETAILS, intent)
    }

    fun onPostCatClicked(postCategory: PostCategory) {
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
            MainFragment.newInstance(-1, postCategory),
            true
        )
    }

    fun onMenuClicked(menuId: Int) {
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
            MainFragment.newInstance(menuId),
            true
        )

    }

    fun newCommentAdded(data: Intent?) {
        //val fragment = childFragmentManager.findFragmentById(R.id.layout_container)
        for(fragment in childFragmentManager.fragments) {
            if (fragment is MainFragment) {
                (fragment as MainFragment).newCommentAdded(data)
            }
        }
    }

    fun toggleReaction(post: ApiPost) {
        //val fragment = childFragmentManager.findFragmentById(R.id.layout_container)
        if(!childFragmentManager.isDestroyed) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment is MainFragment) {
                    (fragment as MainFragment).updateReaction(post)
                }
            }
        }
    }

    fun postDetailsClosed(resultCode: Int, data: Intent?) {
        if (data != null && data.extras != null && data.extras!!.containsKey("post")) {
            val postStr = data.extras!!.getString("post")
            val post = Gson().fromJson(postStr, ApiPost::class.java)

            for(fragment in childFragmentManager.fragments) {
                if (fragment is MainFragment) {
                    if (resultCode == RESULT_POST_DELETED || resultCode == RESULT_POST_REPORTED) {
                        //just delete the post
                        (fragment as MainFragment).postDeleted(post)

                    } else if (resultCode == RESULT_POST_EDITED) {
                        (fragment as MainFragment).postEdited(post)
                    }
                }
            }
        }
    }

    fun newPostAdded(data: Intent?) {
        //val fragment = childFragmentManager.findFragmentById(R.id.layout_container)
        //For post added we need to update all the fragments and need to add post in all
        for(fragment in childFragmentManager.fragments) {
            //debugLog(TAG, "Fragment1 : " + fragment)
            if (fragment is MainFragment) {
                (fragment as MainFragment).postAdded(data)
            }
        }
    }

}