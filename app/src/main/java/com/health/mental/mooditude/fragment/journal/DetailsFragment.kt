package com.health.mental.mooditude.fragment.journal

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.*
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.JournalPrompt
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.databinding.FragmentDetailsBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.utils.DATE_FORMAT_MOOD_TIME
import com.health.mental.mooditude.utils.REQUEST_EDIT_ENTRY
import com.health.mental.mooditude.utils.UiUtils
import org.jetbrains.anko.alert
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment(var mEntry: Entry) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentDetailsBinding

    companion object {
        fun newInstance(entry: Entry): DetailsFragment {
            val fragment = DetailsFragment(entry)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.entry_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                editEntry()
                return true
            }
            R.id.menu_delete -> {
                val activity1 = (requireActivity() as BaseActivity)
                activity1.alert(
                    getString(R.string.are_you_sure_to_delete_entry),
                    getString(R.string.text_delete)
                )
                {
                    positiveButton(R.string.yes) {
                        activity1.showProgressDialog(R.string.please_wait)

                        DBManager.instance.deleteEntry(mEntry, object :
                            FBQueryCompletedListener {
                            override fun onResultReceived(result: Any?) {
                                activity1.hideProgressDialog()
                                //Finish this activity
                                //finish()
                                if (result != null && result is Boolean && result) {
                                    UiUtils.showSuccessToast(
                                        activity1,
                                        getString(R.string.entry_deleted_successfully)
                                    )
                                    (activity1 as JournalActivity).deleteEntry(mEntry)
                                    activity1.onBackPressed()
                                } else {
                                    UiUtils.showErrorToast(
                                        activity1,
                                        getString(R.string.error_text)
                                    )
                                }
                            }
                        })
                    }
                    negativeButton(R.string.no) {}
                }.show()
                return true
            }
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        prepareUi()
        return root
    }

    fun refresh(entry: Entry) {
        this.mEntry = entry
        prepareUi()
    }

    private fun prepareUi() {
        binding.tvDay.setText(SimpleDateFormat("EEEE", Locale.US).format(mEntry.postedDate))
        binding.tvTime.setText(
            SimpleDateFormat(
                DATE_FORMAT_MOOD_TIME,
                Locale.US
            ).format(mEntry.postedDate)
        )

        if(mEntry.emotion != null) {
            binding.ivMood.setImageDrawable(mEntry.emotion!!.getImage(requireContext()))
            binding.ivMood.isSelected = true
            binding.tvIntensity.setText(mEntry.emotionIntensity.toString())
        }
        else {
            binding.ivMood.visibility = View.GONE
            binding.tvIntensity.visibility = View.GONE
        }

        //Add images
        binding.layoutImages.removeAllViews()
        for(activity in mEntry.activities) {
            val itemView = layoutInflater.inflate(R.layout.situation1, binding.layoutImages, false)
            val textView1 = itemView!!.findViewById<TextView>(R.id.tv_text1)
            val textView2 = itemView.findViewById<TextView>(R.id.tv_text2)

            textView1.setText(activity.imageName)
            textView2.setText(activity.title)

            binding.layoutImages.addView(itemView)
        }

        if (mEntry.post != null) {
            binding.tvPost.text = mEntry.post.toString()
            binding.tvPost.visibility = View.VISIBLE
        } else {
            binding.tvPost.visibility = View.GONE
        }

        if (mEntry.imageStr != null && mEntry.imageStr!!.trim().isNotEmpty()) {
            binding.ivImage.visibility = View.VISIBLE
            UiUtils.loadImage(requireContext(), mEntry.imageStr!!, binding.ivImage)
        } else {
            binding.ivImage.visibility = View.GONE
        }

        binding.ivEntrytype.visibility = View.GONE
        binding.tvGuided.visibility = View.GONE
        binding.tvTitle.visibility = View.GONE
        binding.layoutInputs.visibility = View.GONE

        if(mEntry.entryType == EntryType.guidedJournal) {
            binding.ivEntrytype.visibility = View.VISIBLE
            binding.tvGuided.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.VISIBLE
            binding.layoutInputs.visibility = View.VISIBLE
            loadJournalEntry()
        }
        else if(mEntry.entryType == EntryType.journal) {
            binding.ivEntrytype.visibility = View.GONE
        }
    }


    private fun loadJournalEntry() {
        val journalPrompt = JournalPrompt.getPromptFromUserInfo(mEntry.userInfo!!)
        binding.tvTitle.setText(journalPrompt.title)

        binding.layoutInputs.removeAllViews()
        //add steps
        val steps = journalPrompt.steps!!
        for(step in steps) {
            if (step != null && step.input && step.userInput != null) {
                val stepView = LayoutInflater.from(binding.layoutInputs.context).inflate(
                    R.layout.view_entry_step_full,
                    binding.layoutInputs as ViewGroup,
                    false
                )
                val tvStepTitle = stepView.findViewById<TextView>(R.id.tv_step_title)
                val tvStepInput = stepView.findViewById<TextView>(R.id.tv_step_input)

                tvStepTitle.setText(step.title)
                tvStepInput.setText(step.userInput)

                binding.layoutInputs.addView(stepView)
            }
        }
    }

    fun save(): Boolean {
        return false
    }

    private fun editEntry() {
        when(mEntry.entryType) {
            EntryType.mood -> {
                val activity = requireActivity() as BaseActivity
                val intent = Intent(activity, HowYouFeelActivity::class.java)
                intent.putExtra("edit_mode", true)
                intent.putExtra("entry", Gson().toJson(mEntry))
                activity.startActivityForResult(REQUEST_EDIT_ENTRY, intent)
            }
            EntryType.journal -> {
                val activity = requireActivity() as BaseActivity
                val intent = Intent(activity, JournalNewEntryActivity::class.java)
                intent.putExtra("edit_mode", true)
                intent.putExtra("entry", Gson().toJson(mEntry))
                activity.startActivityForResult(REQUEST_EDIT_ENTRY, intent)
            }
            EntryType.guidedJournal -> {
                val activity = requireActivity() as BaseActivity
                val intent = Intent(activity, JournalPromptDetailsActivity::class.java)
                intent.putExtra("edit_mode", true)
                intent.putExtra("entry", Gson().toJson(mEntry))
                activity.startActivityForResult(REQUEST_EDIT_ENTRY, intent)
            }
            else -> {
                //do nothing
            }
        }
    }


}