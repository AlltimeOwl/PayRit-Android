package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentPaymentHistoryDetailBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class PaymentHistoryDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPaymentHistoryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentPaymentHistoryDetailBinding.inflate(layoutInflater)

        return binding.root
    }

}