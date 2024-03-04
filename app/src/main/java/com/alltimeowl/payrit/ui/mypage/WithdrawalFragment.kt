package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentWithdrawalBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class WithdrawalFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentWithdrawalBinding
    private lateinit var spinnerAdapter: WithdrawalSpinnerAdapter

    val TAG = "WithdrawalFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentWithdrawalBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            materialToolbarWithdrawal.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.WITHDRAWAL_FRAGMENT)
                }
            }

            val category = resources.getStringArray(R.array.reason_withdrawal)

            spinnerWithdrawal.run {
                spinnerAdapter = WithdrawalSpinnerAdapter(mainActivity, R.layout.item_withdrawal_spinner, category)
                adapter = spinnerAdapter

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position == 0) {
                            spinnerAdapter.isDropdownOpen = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        spinnerAdapter.isDropdownOpen= false
                    }
                }

            }

        }
    }

}