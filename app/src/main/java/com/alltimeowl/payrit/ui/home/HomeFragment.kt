package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.sampleDataList
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

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.bottomNavigation()
            mainActivity.showBottomNavigationView()

            imageViewBannerCancelHome.setOnClickListener {
                constraintLayoutBannerHome.visibility = View.GONE
            }

            val category = resources.getStringArray(R.array.array_home_time_category)

            spinnerHome.run {
                adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
            }

            recyclerViewHome.run {
                recyclerViewHome.layoutManager = LinearLayoutManager(context)
                adapter = HomeAdapter(mainActivity, sampleDataList)
            }
        }
    }


}