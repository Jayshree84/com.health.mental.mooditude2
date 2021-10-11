package com.health.mental.mooditude.fragment.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.ProfileActivity
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.custom.MultiSpinner
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.FirebaseStorageHelper
import com.health.mental.mooditude.data.model.UserChallenge
import com.health.mental.mooditude.data.model.UserTopGoal
import com.health.mental.mooditude.databinding.FragmentProfileMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileMainFragment() : BaseFragment(), MultiSpinner.MultiSpinnerListener {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentProfileMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val user = DataHolder.instance.getCurrentUser()!!
        if (user.photo.isNotEmpty()) {
            UiUtils.loadProfileImage(user.photo, binding.ivPhoto, R.drawable.ic_profile)
        }
        binding.ivPhoto.setOnClickListener {
            (requireActivity() as BaseActivity).selectImage()
        }

        binding.tvName.setText(user.name)
        binding.tvEmail.setText(user.email)
        val memberSince = SimpleDateFormat(DATE_FORMAT_JOIN, Locale.US).format(user.memberSince)
        binding.tvSince.setText(String.format(getString(R.string.join_date), memberSince))

        //goal
        val goalArry = UserTopGoal.getArray(requireContext())
        val adapterGoal: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, goalArry
        )

        adapterGoal.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerGoal.setAdapter(adapterGoal)
        debugLog(TAG, "user.topGoal : " + user.topGoal)
        if(user.topGoal != null) {
            val position1 = goalArry.indexOf(user.topGoal!!.getLocalizedString(requireContext()))
            debugLog(TAG, "Position1 : " + position1)
            if (position1 != -1) {
                binding.spinnerGoal.setSelection(position1)
            }
        }

        //challenges
        val challengesArry = UserChallenge.getArray(requireContext())
        /*val adapterChallenge: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, challengesArry
        )

        adapterChallenge.setDropDownViewResource(R.layout.spinner_dropdown_item)*/
        val list:List<String> = challengesArry.asList()
        //Create a string to display on spinner
        var challenges = ""
        if(user.topChallenges.isNotEmpty()) {
            val arry = user.topChallenges.split(",")
            for(item in arry) {
                challenges = challenges.plus(UserChallenge.valueOf(item).getLocalizedString(requireContext())).plus(",")
            }
        }
        challenges = challenges.trim(',')

        binding.spinnerChallenges.setItems(list, UserChallenge.getValues(), challenges, getString(R.string.select_challenge), this)
        debugLog(TAG, "user.topChallenges : " + user.topChallenges)
        if(user.topChallenges.isNotEmpty()) {
           val arry = user.topChallenges.split(",")
            for(item in arry) {
                val position1 = challengesArry.indexOf(UserChallenge.valueOf(item).getLocalizedString(requireContext()))
                debugLog(TAG, "item : " + item + " : " + position1)
                if (position1 != -1) {
                    binding.spinnerChallenges.makeSelection(position1)
                }
            }
        }

        binding.tvProfile.setOnClickListener {
            (requireActivity() as ProfileActivity).onProfileBtnClicked()
        }

        return root
    }

    fun updateImageFromCamera(uri: Uri?) {
        if (uri != null) {
            updateImage(uri)
        }
    }

    fun updateImage(selectedImage: Uri) {
        //binding.ivAddImage.setImageURI(selectedImage)
        val path = FileChooser.getBitmap(requireActivity(), selectedImage)
        binding.ivPhoto.setImageBitmap(path)

        //mPhotoUri = selectedImage

        //Let's upload on server
        //Let's first upload pic
        FirebaseStorageHelper.instance.uploadProfileImage(requireActivity(), selectedImage,
            object : FirebaseStorageHelper.OnProgressStatusListener {
                override fun onCompleted(argument: Any?) {

                    if (argument != null) {
                        debugLog(TAG, "Photo URL : " + argument.toString())
                        val photoUrl = argument.toString()
                        DBManager.instance.updateUserPhoto(photoUrl)
                    }
                }
            }, null
        )
    }

    override fun onItemsSelected(selected: BooleanArray?) {

    }

    fun updateUserAttributes() : Boolean {

        var success = false

        //check goal
        val goalPos = binding.spinnerGoal.selectedItemPosition
        if (goalPos == Spinner.INVALID_POSITION) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_goal))
            binding.spinnerGoal.requestFocus()
            return success
        }

        //check challenges
        val selection = binding.spinnerChallenges.getSelectedItemValues()
        if (selection.trim().isEmpty()) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_challenge))
            binding.spinnerChallenges.requestFocus()
            return success
        }

        //update goal
        val position = binding.spinnerGoal.selectedItemPosition + 1
        var goalVal:UserTopGoal? = null
        when(position) {
            1 -> goalVal = UserTopGoal.sleepBetter
            2 -> goalVal = UserTopGoal.handleStress
            3 -> goalVal = UserTopGoal.masterDepression
            4 -> goalVal = UserTopGoal.overcomeAnxiety
            5 -> goalVal = UserTopGoal.controlAnger
            6 -> goalVal = UserTopGoal.boostSelfEsteem
            7 -> goalVal = UserTopGoal.liveHappier
        }

        if(goalVal != null) {
            DBManager.instance.updateUserGoal(goalVal)
        }

        //update challenges
        DBManager.instance.updateUserChallenges(selection)
        debugLog(TAG, "challenges selected : " + selection.toString())

        success = true
        return success
    }
}

