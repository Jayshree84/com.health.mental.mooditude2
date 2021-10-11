package com.health.mental.mooditude.fragment.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.PreferencesActivity
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.databinding.FragmentPreferencesMainBinding
import com.health.mental.mooditude.fragment.BaseFragment
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [PreferencesMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PreferencesMainFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentPreferencesMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPreferencesMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }

    private fun createViews() {
        binding.viewAutoBackup.ivImage.setImageResource(R.drawable.ic_backup)
        binding.viewAutoBackup.tvTitle.setText(R.string.auto_backup)

        //Fetch value from prefernces
        val backup = SharedPreferenceManager.getAutoBackup()
        if(backup == true) {
            binding.viewAutoBackup.tvTitle.isChecked = true
        }
        binding.viewAutoBackup.tvTitle.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferenceManager.setAutoBackup(isChecked)
        }

        //Logout
        binding.viewLogout.ivImage.setImageResource(R.drawable.ic_logout)
        binding.viewLogout.tvTitle.setText(R.string.logout)
        binding.viewLogout.root.setOnClickListener {
            //Logout
            (requireActivity() as BaseActivity).logOutUser()
        }

        //Delete User account
        binding.viewDeleteAccount.ivImage.setImageResource(R.drawable.ic_delete)
        binding.viewDeleteAccount.tvTitle.setText(R.string.delete_account)
        binding.viewDeleteAccount.root.setOnClickListener {
            //Delete account
            (requireActivity() as PreferencesActivity).onDeleteAccountBtnClicked()
        }


    }


}