package com.alltimeowl.payrit.ui.promise

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentPromiseMakeBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class PromiseMakeFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseMakeBinding

    private var amount = ""
    private var promiseStartDate = ""
    private var promiseEndDate = ""
    private var contents = ""
    private var promiseDataList: ArrayList<PromiseData>? = null

    val TAG = "PromiseMakeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseMakeBinding.inflate(layoutInflater)

        amount = arguments?.getString("amount").toString()
        promiseStartDate = arguments?.getString("promiseStartDate").toString()
        promiseEndDate = arguments?.getString("promiseEndDate").toString()
        contents = arguments?.getString("contents").toString()

        promiseDataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("promiseDataList", PromiseData::class.java)
        }else {
            arguments?.getParcelableArrayList("promiseDataList")
        }

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            materialToolbarPromiseMake.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_MAKE_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> mainActivity.showCancelAlertDialog()
                    }
                    false
                }
            }

            recyclerViewPromiseMake.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = PromiseMakeAdapter(binding)
            }

            // 화면에 정보 표시
            textViewAmountPromiseMake.text = amount
            textViewPeriodPromiseMake.text = promiseStartDate + "~" + promiseEndDate
            textViewSenderNamePromiseMake.text = SharedPreferencesManager.getUserName()
            textViewContentsPromiseMake.text = contents

            Log.d(TAG, "promiseDataList : $promiseDataList")

            recyclerViewRecipientPromiseMake.run {
                layoutManager = LinearLayoutManager(context)
                adapter = promiseDataList?.let { RecipientAdapter(it) }
            }

        }
    }

}