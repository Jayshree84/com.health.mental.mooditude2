package com.health.mental.mooditude.activity.ui.selfcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.JournalPrompt
import com.health.mental.mooditude.data.entity.PromptCategory
import com.health.mental.mooditude.databinding.FragmentSelfcareMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils.loadImage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


class MainFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentSelfcareMainBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfcareMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }


    private fun createViews() {

        //Fetch journal categories
        val prompts = loadPrompts()

        //Check for size
        binding.layoutContainer.removeAllViews()
        if (prompts.size > 0) {

            //Now add entries
            val keys = prompts.keys.sortedWith(object : Comparator<PromptCategory> {
                override fun compare(o1: PromptCategory, o2: PromptCategory): Int {
                    return o1.position.compareTo(o2.position)
                }
            })

            debugLog(TAG, "Keys are :" + keys.toString())

            for (category in keys) {
                debugLog(TAG, "Key ::: " + category)

                //Add view
                val promptView = layoutInflater.inflate(
                    R.layout.view_guidedjournal_entry,
                    binding.layoutContainer,
                    false
                )

                val tvCatTitle = promptView.findViewById<TextView>(R.id.tv_title)
                tvCatTitle.setText(category.title)
                binding.layoutContainer.addView(promptView)


                val containerView = promptView.findViewById<ViewGroup>(R.id.entries_container)
                //Let's add category on cardview
                if (category.showCategoryCard) {
                    //show card
                    val entryView =
                        createCardView(containerView, category.title, category.imgStr!!, false, true)
                    entryView.setOnClickListener {
                        (requireActivity() as BaseActivity).showJournalCatDetails(category, entryView.findViewById(R.id.iv_image))
                    }
                    containerView.addView(entryView)
                }
                val list = prompts.get(category)
                if (list != null) {
                    list.sortBy {
                        it.position
                    }
                    for (prompt in list) {

                        val entryView = createCardView(
                            containerView,
                            prompt.title,
                            prompt.imgStr,
                            prompt.isPremium,
                            false
                        )
                        entryView.setOnClickListener {
                            (requireActivity() as BaseActivity).showJournalPromptDetails(prompt, entryView.findViewById(R.id.iv_image))
                        }
                        containerView.addView(entryView)
                    }
                }
            }
        }


    }


    private fun createCardView(
        containerView: ViewGroup,
        title: String,
        imgStr: String,
        isPremium: Boolean,
        isCategory:Boolean

    ): View {
        var entryView = layoutInflater.inflate(R.layout.view_journal_prompt, containerView, false)
        if(isCategory) {
            entryView = layoutInflater.inflate(R.layout.view_journal_prompt_cat, containerView, false)
        }
        val tvTitle = entryView.findViewById<TextView>(R.id.tv_title)
        tvTitle.setText(title)
        val ivImage = entryView.findViewById<ImageView>(R.id.iv_image)

        loadImage(requireContext(), imgStr, ivImage)

        val ivPremium = entryView.findViewById<ImageView>(R.id.iv_premium)
        if (isPremium) {
            //ivPremium.visibility = View.VISIBLE
            ivPremium.visibility = View.GONE
        } else {
            ivPremium.visibility = View.GONE
        }

        return entryView
    }

    private fun loadPrompts(): LinkedHashMap<PromptCategory, ArrayList<JournalPrompt>> {
        val map = LinkedHashMap<PromptCategory, ArrayList<JournalPrompt>>()
        try {

            val allCategories = DBManager.instance.getAllJournalPromptCategories()
            debugLog(TAG, "Journal prompt categories : " + allCategories.size)

            val allPrompts = DBManager.instance.getAllJournalPrompts()
            debugLog(TAG, "Journal prompts : " + allPrompts.size)

            val map2 = LinkedHashMap<String, ArrayList<JournalPrompt>>()
            for (prompt in allPrompts) {
                if (map2.get(prompt.groupId) == null) {
                    map2.put(prompt.groupId, ArrayList())
                }
                map2.get(prompt.groupId)!!.add(prompt)
            }

            for (cat in allCategories) {
                if (map2.get(cat.categoryId) != null && map2.get(cat.categoryId)!!.size > 0) {
                    map.put(cat, map2.get(cat.categoryId)!!)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return map
    }

}
