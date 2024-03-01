package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentHomeBinding
import com.alltimeowl.payrit.ui.main.MainActivity


class HomeFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentHomeBinding

    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentHomeBinding.inflate(layoutInflater)

        initUI()
        switchButton()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.bottomNavigation()

            val category = resources.getStringArray(R.array.array_home_time_category)

            spinnerHome.run {
                adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
            }
        }
    }

    private fun switchButton() {

        // 빌려준 기록 클릭
        binding.buttonLoanRecordHome.setOnClickListener {
            binding.buttonLoanRecordHome.setBackgroundResource(R.drawable.bg_semicircle_mint_r12)
            binding.buttonLoanRecordHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.buttonBorrowedRecordHome.setBackgroundResource(R.drawable.bg_semicircle_white_r12)
            binding.buttonBorrowedRecordHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray100))
        }

        // 빌린 기록 클릭
        binding.buttonBorrowedRecordHome.setOnClickListener {
            binding.buttonLoanRecordHome.setBackgroundResource(R.drawable.bg_semicircle_white_r12 )
            binding.buttonLoanRecordHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray100))
            binding.buttonBorrowedRecordHome.setBackgroundResource(R.drawable.bg_semicircle_mint_r12)
            binding.buttonBorrowedRecordHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

    }

}