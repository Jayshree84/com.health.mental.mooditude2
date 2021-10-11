package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.model.PaymentMethod
import com.health.mental.mooditude.data.model.TherapistRequestInfo
import com.health.mental.mooditude.databinding.FragmentPaymentMethodBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils


/**
 * A simple [Fragment] subclass.
 * Use the [PaymentMethodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentMethodFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentPaymentMethodBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val text = getString(R.string.how_you_pay_desc)
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvDesc.setText(Html.fromHtml(text, 0))
        } else {
            binding.tvDesc.setText(Html.fromHtml(text))
        }

        return root
    }

    fun updatePaymentMethod(requestInfo: TherapistRequestInfo): Boolean {
        var isSelected = false
        if(binding.rbUnknown.isChecked) {
            requestInfo.paymentMethod = PaymentMethod.unknown.getTitle(requireContext())
            isSelected = true
        }
        else if(binding.rbCash.isChecked) {
            requestInfo.paymentMethod = PaymentMethod.cash.getTitle(requireContext())
            isSelected = true
        }
        else if(binding.rbInsurance.isChecked) {
            requestInfo.paymentMethod = PaymentMethod.insurance.getTitle(requireContext())
            isSelected = true
        }

        if(!isSelected) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_payment_method))
            return false
        }

        //save user property
        //FirebaseDBManager.instance.updateUserPaymentMethod(requestInfo.paymentMethod)
        return true
    }


}