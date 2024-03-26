package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentHomeBinding
import com.alltimeowl.payrit.ui.main.MainActivity


class HomeFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentHomeBinding
    private lateinit var spinnerAdapter: SpinnerAdapter

    private lateinit var viewModel: HomeViewModel

    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentHomeBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.loadMyIouList(accessToken)

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.bottomNavigation()
            mainActivity.showBottomNavigationView()

            materialToolbarHome.run {
                title = SharedPreferencesManager.getUserName() + "님의 기록"
            }

            imageViewBannerCancelHome.setOnClickListener {
                constraintLayoutBannerHome.visibility = View.GONE
            }

            val category = resources.getStringArray(R.array.array_home_time_category)

            spinnerHome.run {
                spinnerAdapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
                adapter = spinnerAdapter

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        viewModel.iouList.value?.let { list ->
                            val sortedList = when (position) {
                                0 -> list.sortedByDescending { it.transactionDate } // position이 0일 때 내림차순 정렬
                                1 -> list.sortedBy { it.transactionDate } // position이 1일 때 올림차순 정렬
                                2 -> list.sortedBy { it.dueDate }
                                else -> list
                            }

                            Log.d(TAG, "스피너 클릭시 정렬 되는 sortedList : ${sortedList}")

                            // 정렬된 리스트로 UI 업데이트
                            (binding.recyclerViewHome.adapter as HomeAdapter).updateData(sortedList)

                            if (sortedList.isEmpty()) {
                                binding.linearLayoutNonexistenceTransactionHome.visibility = View.VISIBLE
                                binding.linearLayoutExistenceTransactionHome.visibility = View.GONE
                            } else {
                                binding.linearLayoutNonexistenceTransactionHome.visibility = View.GONE
                                binding.linearLayoutExistenceTransactionHome.visibility = View.VISIBLE
                                binding.textViewIouNumberHome.text = "총 ${sortedList.size}건"
                            }

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

            recyclerViewHome.run {
                recyclerViewHome.layoutManager = LinearLayoutManager(context)
                adapter = HomeAdapter(mainActivity, mutableListOf())
            }
        }
    }

    // LiveData 관찰
    private fun observeData() {
        viewModel.iouList.observe(viewLifecycleOwner) { iouList ->
            val sortedList = iouList.sortedByDescending { it.transactionDate }

            binding.progressBarLoadingHome.visibility = View.GONE

            // 데이터가 변경될 때마다 UI 업데이트
            (binding.recyclerViewHome.adapter as HomeAdapter).updateData(sortedList)

            if (sortedList.isEmpty()) {
                binding.linearLayoutNonexistenceTransactionHome.visibility = View.VISIBLE
                binding.linearLayoutExistenceTransactionHome.visibility = View.GONE
            } else {
                binding.linearLayoutNonexistenceTransactionHome.visibility = View.GONE
                binding.linearLayoutExistenceTransactionHome.visibility = View.VISIBLE
                binding.textViewIouNumberHome.text = "총 ${sortedList.size}건"
            }

        }

    }
}