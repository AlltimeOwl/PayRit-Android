package com.alltimeowl.payrit.ui.home

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouDetailAmountReceivedBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class IouDetailAmountReceivedFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailAmountReceivedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouDetailAmountReceivedBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouDetailAmountReceived.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_DETAIL_AMOUNT_RECEIVED_FRAGMENT)
                }
            }

            // 받은 날짜
            textInputLayoutDateIouDetailAmountReceived.setEndIconOnClickListener {

                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                context?.let { it1 ->
                    DatePickerDialog(it1, { _, year, month, day ->
                        run {
                            binding.editTextDateIouDetailAmountReceived.setText(year.toString() + "." + (month + 1).toString() + "." + day.toString())
                        }
                    }, year, month, day)
                }?.show()

            }

            recyclerViewIouDetailAmountReceived.run {
                recyclerViewIouDetailAmountReceived.layoutManager = LinearLayoutManager(context)
                adapter = IouDetailAdapter()
            }

        }
    }

}