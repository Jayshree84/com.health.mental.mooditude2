package com.health.mental.mooditude.activity.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.HowYouFeelActivity
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.FirebaseStorageHelper
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.data.model.M3DisorderIntensity
import com.health.mental.mooditude.data.model.journal.EmotionType
import com.health.mental.mooditude.databinding.FragmentHomeMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.*
import com.health.mental.mooditude.utils.UiUtils.loadProfileImage

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.

    companion object {
        //static object
        private var isGoalDisplayed = false
    }

    private val binding get() = _binding!! as FragmentHomeMainBinding
    private var mCurrentUser: AppUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (requireActivity() as BaseActivity).checkForNewAssessment(binding.layoutAddAssessment.root)

        //update user details
        updateUserDetails()

        binding.layoutFeeling.ivMoodNormal.setOnClickListener {
            startHowYouFeel(EmotionType.normal, 1)
        }
        binding.layoutFeeling.tvNormal.setOnClickListener {
            startHowYouFeel(EmotionType.normal, 1)
        }
        binding.layoutFeeling.ivMoodLow.setOnClickListener {
            startHowYouFeel(EmotionType.low, 1)
        }
        binding.layoutFeeling.tvLow.setOnClickListener {
            startHowYouFeel(EmotionType.low, 1)
        }
        binding.layoutFeeling.ivMoodWorst.setOnClickListener {
            startHowYouFeel(EmotionType.worst, 1)
        }
        binding.layoutFeeling.tvWorst.setOnClickListener {
            startHowYouFeel(EmotionType.worst, 1)
        }
        binding.layoutFeeling.ivMoodHigh.setOnClickListener {
            startHowYouFeel(EmotionType.high, 1)
        }
        binding.layoutFeeling.tvHigh.setOnClickListener {
            startHowYouFeel(EmotionType.high, 1)
        }
        binding.layoutFeeling.ivMoodElevated.setOnClickListener {
            startHowYouFeel(EmotionType.elevated, 1)
        }
        binding.layoutFeeling.tvElevated.setOnClickListener {
            startHowYouFeel(EmotionType.elevated, 1)
        }

        return root
    }

    private fun startHowYouFeel(type: EmotionType, intensity: Int) {
        val intent = Intent(requireActivity(), HowYouFeelActivity::class.java)
        intent.putExtra("type", type.toString())
        intent.putExtra("intensity", intensity)
        startActivity(intent)
    }

    fun updateUserDetails() {
        mCurrentUser = DataHolder.instance.getCurrentUser()

        if (mCurrentUser != null) {
            binding.tvName.text =
                mCurrentUser!!.name //String.format(getString(R.string.name_welcome), mCurrentUser!!.name)
            binding.tvGreeting.text = CalendarUtils.getGreetingsText(requireContext())
            if (!isGoalDisplayed) {
                binding.tvGoal.text = String.format(
                    getString(R.string.goal_text),
                    mCurrentUser!!.topGoal!!.getLocalizedString(requireContext())
                )
                isGoalDisplayed = true
            } else {
                val index = getRandomNumber(1, 30)
                binding.tvGoal.text = getStringFromName(requireContext(), "WELCOME_MSG_"+index)
            }

            if (mCurrentUser!!.photo.isNotEmpty()) {
                loadProfileImage(mCurrentUser!!.photo, binding.ivPhoto)
            }
            binding.ivPhoto.setOnClickListener {
                //Check for permission
                //(requireActivity() as BaseActivity).selectImage()
            }
        }

        //fetch user profile
        val profile = DataHolder.instance.getCurrentUserProfile()
        if (profile != null) {
            binding.ivCheck.visibility = View.GONE
            binding.tvCheck.visibility = View.GONE
            binding.ivStar.visibility = View.GONE
            binding.tvStar.visibility = View.GONE
            binding.ivCrown.visibility = View.GONE
            binding.tvCrown.visibility = View.GONE
            //Badge stats -- check for counts
            /*
                if (profile.stats.checksCount > 0) {
                    binding.ivCheck.visibility = View.VISIBLE
                    binding.tvCheck.visibility = View.VISIBLE
                    binding.tvCheck.text = profile.stats.checksCount.toString()
                } else {
                    binding.ivCheck.visibility = View.GONE
                    binding.tvCheck.visibility = View.GONE
                }

                if (profile.stats.starCount > 0) {
                    binding.ivStar.visibility = View.VISIBLE
                    binding.tvStar.visibility = View.VISIBLE
                    binding.tvStar.text = profile.stats.starCount.toString()
                } else {
                    binding.ivStar.visibility = View.GONE
                    binding.tvStar.visibility = View.GONE
                }

                if (profile.stats.crownsCount > 0) {
                    binding.ivCrown.visibility = View.VISIBLE
                    binding.tvCrown.visibility = View.VISIBLE
                    binding.tvCrown.text = profile.stats.crownsCount.toString()
                } else {
                    binding.ivCrown.visibility = View.GONE
                    binding.tvCrown.visibility = View.GONE
                }

                */
        }
    }
}