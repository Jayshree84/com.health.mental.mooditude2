package com.health.mental.mooditude.activity.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.databinding.FragmentCareMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCareMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCareMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnNeedTherapist.setOnClickListener {
            (requireActivity() as BaseActivity).onNeedTherapistBtnClicked()
        }

        //Let's first check if we have any request
        //layout_request_container
        DBManager.instance.getTherapistRequestList().observe(viewLifecycleOwner, {
            val list = it
            if(list == null || list.size == 0) {
                binding.layoutRequestContainer.removeAllViews()
                binding.layoutRequestContainer.visibility = View.GONE
                binding.layoutMakeRequest.visibility = View.VISIBLE
                return@observe
            }
            if (list.size > 0) {
                //add widget
                var visibleMakeRequest = true
                for (item in list) {
                    if (item.deleted) continue

                    //check for posted time
                    val diffHours = CalendarUtils.getDiffInHours(
                        item.postedDate,
                        Date(System.currentTimeMillis())
                    )

                    if (diffHours > 48 || item.feedback) {
                        //Just skip this request
                        debugLog(TAG,"Skipping request : " + item.postedDate)
                        continue
                    }

                    //Else add view
                    //val nextDay = CalendarUtils.getNextDay(item.postedDate)

                    val widget = inflater.inflate(
                        R.layout.widget_therapist_new_request,
                        binding.layoutRequestContainer,
                        false
                    )
                    val tvDesc = widget.findViewById<TextView>(R.id.tv_desc)
                    val tvDavte = widget.findViewById<TextView>(R.id.tv_date)
                    //06/23/2021

                    val textDate = String.format(
                        getString(R.string.requested_date),
                        SimpleDateFormat("MM/dd/yyyy", Locale.US).format(item.postedDate)
                    )
                    tvDavte.setText(textDate)

                    binding.layoutRequestContainer.visibility = View.VISIBLE
                    binding.layoutRequestContainer.addView(widget)

                    val btn1 = widget.findViewById<AppCompatButton>(R.id.btn_had_call)
                    val btn2 = widget.findViewById<AppCompatButton>(R.id.btn_waiting_for_call)

                    if (diffHours < 24) {
                        tvDesc.setText(getString(R.string.member_of_provider_will_contact_you))
                        //show this item without any button
                        btn1.visibility = View.GONE
                        btn2.visibility = View.GONE

                        //Remove view to find therapist
                        visibleMakeRequest = false
                    }
                    else {
                        tvDesc.setText(getString(R.string.provider_team_will_contact_you))

                        btn1.visibility = View.VISIBLE
                        btn2.visibility = View.VISIBLE
                        btn1.setOnClickListener {
                            (requireActivity() as BaseActivity).showTherapistFeedbackPage(
                                item.requestId
                            )
                        }
                        btn2.setOnClickListener {
                            /*(requireActivity() as BaseActivity).showTherapistFeedbackPage(
                                item.requestId
                            )*/
                            //Open chat view
                        }
                    }
                }
                //
                if(visibleMakeRequest) {
                    binding.layoutMakeRequest.visibility = View.VISIBLE
                }
                else {
                    binding.layoutMakeRequest.visibility = View.GONE
                }
            }
        })

        return root
    }




}