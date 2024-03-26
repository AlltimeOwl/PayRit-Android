package com.alltimeowl.payrit.ui.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.data.model.MemoRequest
import com.alltimeowl.payrit.databinding.FragmentIouDetailMemoBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.util.Date
import java.util.Locale

class IouDetailMemoFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailMemoBinding

    private lateinit var viewModel: HomeViewModel

    private var paperId = 0
    private var content = ""

    val TAG = "IouDetailMemoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentIouDetailMemoBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        paperId = arguments?.getInt("paperId", paperId)!!

        MainActivity.accessToken?.let { viewModel.getIouDetail(it, paperId) }

        initUI()
        observeData()

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
                adapter = IouDetailMemoAdapter(mutableListOf())
            }


            // 메모 입력
            editTextMemoIouDetailMemo.addTextChangedListener(
                getTextWatcher(editTextMemoIouDetailMemo)
            )

            // 입력 하기 버튼
            buttonInputIouDetailMemo.setOnClickListener {
                inputMemo()
            }

        }
    }

    private fun getTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()

                content = text.ifEmpty {
                    ""
                }

            }

        }
    }

    private fun inputMemo() {
        if (content.isNotEmpty()) {
            val memoRequest = MemoRequest(content)
            MainActivity.accessToken?.let { viewModel.postMemo(it, paperId, memoRequest) }
            binding.editTextMemoIouDetailMemo.text.clear()
        } else {
            return
        }
    }

    private fun observeData() {
        viewModel.memoList.observe(viewLifecycleOwner) { memoList ->
            val sortedMemoList = memoList.sortedByDescending { it.createdAt }
            (binding.recyclerViewIouDetailMemo.adapter as IouDetailMemoAdapter).updateData(sortedMemoList)
        }
    }

}