package com.health.mental.mooditude.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.adapter.CircleColorAdapter
import com.health.mental.mooditude.databinding.FragmentSelectThemeBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [SelectThemeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectThemeFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentSelectThemeBinding

    inner class AppTheme(val color: Int, val themeId: String) {
    }

    // Keep all Images in array
    private var mThemeList = arrayOf<AppTheme>(
        AppTheme(R.color.theme1, "theme1"),
        AppTheme(R.color.theme2, "theme2"),
        AppTheme(R.color.theme3, "theme3"),
        AppTheme(R.color.theme4, "theme4"),
        AppTheme(R.color.theme5, "theme5"),
        AppTheme(R.color.theme6, "theme6"),
        AppTheme(R.color.theme7, "theme7"),
        AppTheme(R.color.theme8, "theme8"),
        AppTheme(R.color.theme9, "theme9"),
        AppTheme(R.color.theme10, "theme10"),
        AppTheme(R.color.theme11, "theme11"),
        AppTheme(R.color.theme12, "theme12"),
        AppTheme(R.color.theme13, "theme13"),
        AppTheme(R.color.theme14, "theme14"),
        AppTheme(R.color.theme15, "theme15"),
        AppTheme(R.color.theme16, "theme16")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectThemeBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvTitle.text = getString(R.string.select_theme_title)
        binding.tvDesc.text = getString(R.string.select_theme_desc)

        setNextEnabled(true)
        binding.btnContinue.setOnClickListener {
            //(requireActivity() as SetUserProfileActivity).onThemeSelected(true)
        }

        binding.gridview.setAdapter(CircleColorAdapter(requireActivity(), mThemeList))

        binding.gridview.setOnItemClickListener(OnItemClickListener { parent, v, position, id -> // Send intent to SingleViewActivity
            //selectedTheme = mThemeList.get(position)
            binding.selection.setColor(ContextCompat.getColor(requireActivity(), mThemeList.get(position).color ))
        })
        return view
    }

    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        binding.btnContinue.isEnabled = flag
    }
}