package com.health.mental.mooditude.fragment

import android.content.Context
import android.os.Looper
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.EmailRegistrationActivity
import com.health.mental.mooditude.activity.HomeActivity
import com.health.mental.mooditude.activity.ui.care.*
import com.health.mental.mooditude.activity.ui.community.MainFragment
import com.health.mental.mooditude.activity.ui.community.PostCatDetailsFragment
import com.health.mental.mooditude.activity.ui.tracking.AssessmentListFragment
import com.health.mental.mooditude.activity.ui.tracking.FullReportFragment
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.registration.EmailFragment


/**
 * Created by Jayshree.Rathod on 21-08-2017.
 */
open class BaseFragment : Fragment() {

    protected val TAG = this.javaClass.simpleName

    //For view binding
    protected var _binding: ViewBinding? = null

    //private members
    private var mEditTextWithWatcher: EditText? = null
    private var mTextWatcher: TextWatcher? = null


    /**
     * Event Listener
     */
    protected val editorEventListener = object : TextView.OnEditorActionListener {

        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            var ret: Boolean = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                if (activity != null && activity is EmailRegistrationActivity) {
                }
            }
            return ret
        }
    }


    /**
     * Performs common API calls for phone registration
     */
    protected fun initializeEditText(editText: EditText, textWatcher: TextWatcher) {
        mEditTextWithWatcher = editText
        mTextWatcher = textWatcher

        //show keyboard
        val im =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(editText, 0)

        //add textwatcher
        editText.addTextChangedListener(textWatcher)
        //editText.setOnEditorActionListener(editorEventListener)

        addTouchListener(editText)
    }

    /**
     * Adds touch listener to handle touchevent on clear button
     */
    protected fun addTouchListener(editText: EditText) {
        editText.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent != null && motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (motionEvent.x >= (view.width - (view as EditText).compoundPaddingRight)) {
                    clearText(view)
                }
            }
            return@OnTouchListener false
        })
    }


    protected fun clearText(view: EditText) {
        view.setText("")

        //show keyboard
        view.requestFocus()
    }

    /**
     * Show/hides clear button
     */
    protected fun updateClearButton(editText: EditText, s: CharSequence) {
        if (s.length > 0) {
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_clear), null
            )
        } else {
            editText.setCompoundDrawables(null, null, null, null)
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        if(mEditTextWithWatcher != null && mTextWatcher != null) {
            mEditTextWithWatcher!!.removeTextChangedListener(mTextWatcher)
        }
    }*/


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        android.os.Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }

    fun showError(exception: Exception) {
        val tvError = _binding!!.root.findViewById<TextView>(R.id.tv_error_info)
        if (tvError != null && exception.localizedMessage != null) {
            tvError.text = exception.localizedMessage
        }
    }


    @Throws(IllegalStateException::class)
    protected fun addFragment(
        fragmentContainerResourceId: Int, nextFragment: Fragment, removeAll: Boolean = false,
        animationOn: Boolean = true, isDrawer: Boolean = false
    ): Boolean {

        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //Show Animation
        if (animationOn) {
            fragmentTransaction.setCustomAnimations(
                R.anim.anim_right_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_right_out
            )
        } else if (isDrawer) {
            fragmentTransaction.setCustomAnimations(
                R.anim.zoom_enter,
                0
            )
        }


        if (removeAll) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment != null) {
                    fragmentTransaction.remove(fragment)
                }
            }

            //remove all back stack entries
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fragmentTransaction.add(
            fragmentContainerResourceId,
            nextFragment,
            nextFragment.javaClass.simpleName
        )
        fragmentTransaction.commit()
        return true
    }


    /**
     * Add fragment into desire fragment container layout
     * @param fragmentContainerResourceId fragment container resource  id
     * *
     * @param currentFragment             current added fragment into same container
     * *
     * @param nextFragment                the fragment which now we want to add above current fragment in same container
     * *
     * @return true if fragment added successfully, false otherwise
     * *
     * @throws IllegalStateException throws in case of transaction after activity saved its state
     */
    @Throws(IllegalStateException::class)
    protected fun addFragment(
        fragmentContainerResourceId: Int, currentFragment: Fragment?,
        nextFragment: Fragment, animationOn: Boolean = true
    ): Boolean {

        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        //fragmentTransaction.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out,R.anim.anim_right_in, R.anim.anim_right_out)
        if (animationOn) {
            fragmentTransaction.setCustomAnimations(
                R.anim.anim_right_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_right_out
            )
        }

        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        fragmentTransaction.add(
            fragmentContainerResourceId,
            nextFragment,
            nextFragment.javaClass.simpleName
        )
        fragmentTransaction.addToBackStack(nextFragment.javaClass.simpleName)

        fragmentTransaction.commit()
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //set toolbar icon
        if (this is FullReportFragment) {
            if (activity is HomeActivity) {
                (requireActivity() as HomeActivity).showCloseBtn(true)
                (requireActivity() as BaseActivity).setPageTitle(
                    requireActivity().findViewById(R.id.main_toolbar),
                    getString(R.string.mental_wellbeing_score)
                )
            }
        } else if (this is AssessmentListFragment) {
            (requireActivity() as HomeActivity).showCloseBtn(true)
            (requireActivity() as BaseActivity).setPageTitle(
                requireActivity().findViewById(R.id.main_toolbar),
                getString(R.string.mental_wellbeing_score)
            )
        } else if (this is HasAssessmentFragment) {
            (requireActivity() as HomeActivity).showCloseBtn()
            (requireActivity() as BaseActivity).setPageTitle(
                requireActivity().findViewById(R.id.main_toolbar),
                getString(R.string.mental_wellbeing_score)
            )
        } else if (this is StateFragment ||
            this is NoStateOperatingFragment
        ) {
            (requireActivity() as HomeActivity).showCloseBtn()
            (requireActivity() as BaseActivity).setPageTitle(
                requireActivity().findViewById(R.id.main_toolbar),
                ""
            )
        } else if (this is EmailFragment) {
            (requireActivity() as EmailRegistrationActivity).showCloseBtn()
        } else if (this is PostCatDetailsFragment) {
            (requireActivity() as HomeActivity).showCloseBtn(true)
            (requireActivity() as BaseActivity).setPageTitle(
                requireActivity().findViewById(R.id.main_toolbar),
                this.getCategoryTitle()
            )
        }

        debugLog(TAG, "ATTACH : " + this.javaClass.simpleName)
    }

    override fun onDetach() {
        super.onDetach()

        debugLog(TAG, "DETACH : " + this.javaClass.simpleName)
        if (this is FullReportFragment || this is AssessmentListFragment) {

            if (requireActivity() is HomeActivity) {

                val activity = requireActivity() as HomeActivity
                if (parentFragmentManager.backStackEntryCount == 0) {
                    activity.showHomeBtn()

                    activity.setPageTitle(
                        requireActivity().findViewById(R.id.main_toolbar),
                        getString(R.string.pagetitle_tracking)
                    )
                }
            }
        } else if (this is PostCatDetailsFragment) {

            if (requireActivity() is HomeActivity) {

                val activity = requireActivity() as HomeActivity
                if (parentFragmentManager.backStackEntryCount == 0) {
                    activity.showHomeBtn()

                    activity.setPageTitle(
                        requireActivity().findViewById(R.id.main_toolbar),
                        getString(R.string.pagetitle_community)
                    )
                }
            }
        } else if (this is MainFragment) {

            if (requireActivity() is HomeActivity) {

                //now check if it's not filtered
                val filtered = (this as MainFragment).isFilteringSelected()

                if(filtered) {
                    val activity = requireActivity() as HomeActivity
                    if (parentFragmentManager.backStackEntryCount == 0) {
                        activity.showHomeBtn()

                        activity.setPageTitle(
                            requireActivity().findViewById(R.id.main_toolbar),
                            getString(R.string.pagetitle_community)
                        )
                    }
                }
            }
        }

        //For drawer icon when tab is selected
        if (requireActivity() is HomeActivity) {
            val activity = requireActivity() as HomeActivity
            if (activity.supportFragmentManager.backStackEntryCount == 0) {
                activity.showHomeBtn()
                //show bottom menu
            }
        }

    }

    protected fun addScrollListener(listView: RecyclerView) {
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var count = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // Scrolling up
                    hideBottomMenu()
                } else {
                    // Scrolling down
                    showBottomMenu()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }
        })

    }

    protected fun hideBottomMenu() {
        (context as? HomeActivity)?.slideDown()
    }

    protected fun showBottomMenu() {
        (context as? HomeActivity)?.slideUp()
    }


}
