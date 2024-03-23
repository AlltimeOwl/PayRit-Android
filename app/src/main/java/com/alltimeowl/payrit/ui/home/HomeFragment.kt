package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentHomeBinding
import com.alltimeowl.payrit.ui.main.MainActivity


class HomeFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: IouViewModel

    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentHomeBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[IouViewModel::class.java]

        MainActivity.accessToken?.let { viewModel.loadMyIouList(it) }

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.bottomNavigation()
            mainActivity.showBottomNavigationView()

            materialToolbarHome.run {
                title = MainActivity.loginUserName + "님의 기록"
            }

            imageViewBannerCancelHome.setOnClickListener {
                constraintLayoutBannerHome.visibility = View.GONE
            }

            val category = resources.getStringArray(R.array.array_home_time_category)

            spinnerHome.run {
                adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
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
            // 데이터가 변경될 때마다 UI 업데이트
            (binding.recyclerViewHome.adapter as HomeAdapter).updateData(iouList)
            Log.d(TAG, "iouList : ${iouList}")

            if (iouList.isEmpty()) {
                binding.linearLayoutNonexistenceTransactionHome.visibility = View.VISIBLE
                binding.linearLayoutExistenceTransactionHome.visibility = View.GONE
            } else {
                binding.linearLayoutNonexistenceTransactionHome.visibility = View.GONE
                binding.linearLayoutExistenceTransactionHome.visibility = View.VISIBLE
                binding.textViewIouNumberHome.text = "총 ${iouList.size}건"
            }

        }

    }
}