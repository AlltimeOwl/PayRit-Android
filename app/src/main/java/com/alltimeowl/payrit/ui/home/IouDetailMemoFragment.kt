package com.alltimeowl.payrit.ui.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.sampleMemoDataList
import com.alltimeowl.payrit.databinding.FragmentIouDetailMemoBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.util.Date
import java.util.Locale

class IouDetailMemoFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailMemoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentIouDetailMemoBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouDetailMemo.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_DETAIL_MEMO_FRAGMENT)
                }
            }

            // 오늘 날짜로 설정
            val currentDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())
            textViewCurrentDateIouDetailMemo.text = currentDate

            recyclerViewIouDetailMemo.run {
                recyclerViewIouDetailMemo.layoutManager = LinearLayoutManager(context)
                adapter = IouDetailMemoAdapter(sampleMemoDataList)
            }

        }
    }

}