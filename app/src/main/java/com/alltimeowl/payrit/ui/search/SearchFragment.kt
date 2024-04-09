package com.alltimeowl.payrit.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.databinding.FragmentSearchBinding
import com.alltimeowl.payrit.ui.home.HomeAdapter
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.home.SpinnerAdapter
import com.alltimeowl.payrit.ui.main.MainActivity

class SearchFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentSearchBinding
    private lateinit var spinnerAdapter: SpinnerAdapter

    private lateinit var viewModel: HomeViewModel

    val TAG = "SearchFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentSearchBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.loadMyIouList(accessToken)

        initUi()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNavigationView()
        mainActivity.showKeyboard(binding.editTextSearch)
    }

    private fun initUi() {
        binding.run {
            materialToolbarSearch.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.SEARCH_FRAGMENT)
                }
            }

            editTextSearch.addTextChangedListener(
                etTextWatcher(editTextSearch)
            )

            recyclerViewSearch.run {
                recyclerViewSearch.layoutManager = LinearLayoutManager(context)
                adapter = HomeAdapter(mainActivity, mutableListOf())
            }

        }
    }

    private fun etTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    observeData(it.toString())
                }
            }

        }
    }

    private fun observeData(searchText: String) {
        viewModel.iouList.observe(viewLifecycleOwner) { iouList ->
            // 데이터가 변경될 때마다 현재 검색어에 맞게 필터링
            filterList(searchText, iouList)
        }
    }

    // iouList에서 peerName을 검색하여 해당하는 항목들만 필터링하고 리사이클러뷰를 업데이트하는 메서드
    private fun filterList(searchText: String, iouList: List<getMyIouListResponse>) {

        val filteredList = if (searchText.isEmpty()) {
            emptyList()
        } else {
            iouList.filter {
                it.peerName.contains(searchText, ignoreCase = true)
            }
        }

        if (filteredList.isEmpty()) {
            binding.linearLayoutExistenceTransactionSearch.visibility = View.GONE
        } else {
            binding.linearLayoutExistenceTransactionSearch.visibility = View.VISIBLE
            binding.textViewIouNumberSearch.text = "총 ${filteredList.size}건"
            settingSpinner(filteredList)
        }

        // 필터링된 리스트로 리사이클러뷰 업데이트
        (binding.recyclerViewSearch.adapter as HomeAdapter).updateData(filteredList)
    }

    private fun settingSpinner(filteredList: List<getMyIouListResponse>) {
        val category = resources.getStringArray(R.array.array_home_time_category)

        binding.spinnerSearch.run {
            spinnerAdapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
            adapter = spinnerAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    filteredList.let { list ->
                        val sortedList = when (position) {
                            0 -> list.sortedByDescending { it.transactionDate }
                            1 -> list.sortedBy { it.transactionDate }
                            2 -> list.sortedBy { it.dueDate }
                            else -> list
                        }

                        Log.d(TAG, "스피너 클릭시 정렬 되는 sortedList : ${sortedList}")

                        (binding.recyclerViewSearch.adapter as HomeAdapter).updateData(sortedList)

                    }

                    if (position == 0) {
                        spinnerAdapter.isDropdownOpen = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    spinnerAdapter.isDropdownOpen = false
                }

            }

        }
    }

}