package com.alltimeowl.payrit.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.alltimeowl.payrit.BuildConfig
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.PromiseDetail
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.data.model.SharedPreferencesPromiseManager
import com.alltimeowl.payrit.data.model.UserCertificationResponse
import com.alltimeowl.payrit.databinding.FragmentHomeBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.alltimeowl.payrit.ui.promise.PromiseViewModel
import com.alltimeowl.payrit.ui.write.WriteMainViewModel
import com.iamport.sdk.data.sdk.IamPortCertification
import com.iamport.sdk.domain.core.Iamport
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


class HomeFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentHomeBinding
    private lateinit var spinnerAdapter: SpinnerAdapter

    private lateinit var viewModel: HomeViewModel
    private lateinit var writeMainViewModel: WriteMainViewModel
    private lateinit var promiseViewModel: PromiseViewModel

    private var accessToken = ""

    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentHomeBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        writeMainViewModel = ViewModelProvider(this)[WriteMainViewModel::class.java]
        promiseViewModel = ViewModelProvider(this)[PromiseViewModel::class.java]

        accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.loadMyIouList(accessToken)
        promiseViewModel.getMyPromiseList(accessToken)

        val promiseId = arguments?.getString("promiseId")

        if (promiseId != null && !SharedPreferencesPromiseManager.isPromiseIdProcessed(promiseId)) {
            // promiseId가 처리되지 않았다면, 여기서 처리
            promiseViewModel.sharePromise(accessToken, promiseId.toInt(),
                onSuccess = {
                    promiseViewModel.getMyPromiseList(accessToken)
                    SharedPreferencesPromiseManager.savePromiseId(promiseId) // 처리 후 promiseId 저장
                }
            )
        }

        initUI()
        observeData()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Iamport.init(this@HomeFragment)
    }

    private fun initUI() {
        binding.run {
            mainActivity.bottomNavigation()
            mainActivity.showBottomNavigationView()

            materialToolbarHome.run {
                title = SharedPreferencesManager.getUserName() + "님의 기록"

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.item_search -> {

                            mainActivity.replaceFragment(MainActivity.SEARCH_FRAGMENT, true, null)

                            true
                        }

                        else -> false
                    }
                }

            }

            // 페이릿 클릭시
            textViewPayritTypeHome.setOnClickListener {
                settingUiType(true)
                viewPayritLine.background = ContextCompat.getDrawable(requireContext(), R.color.primaryMint)
                viewPromiseLine.background = ContextCompat.getDrawable(requireContext(), R.color.white100)
            }

            // 약속 클릭시
            textViewPromiseTypeHome.setOnClickListener {
                settingUiType(false)
                viewPayritLine.background = ContextCompat.getDrawable(requireContext(), R.color.white100)
                viewPromiseLine.background = ContextCompat.getDrawable(requireContext(), R.color.primaryMint)
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
                                binding.buttonGuideHome.visibility = View.VISIBLE
                            } else {
                                binding.linearLayoutNonexistenceTransactionHome.visibility = View.GONE
                                binding.linearLayoutExistenceTransactionHome.visibility = View.VISIBLE
                                binding.textViewIouNumberHome.text = "총 ${sortedList.size}건"
                                binding.buttonGuideHome.visibility = View.GONE
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

            viewPagerBannerHome.run {
                viewPagerBannerHome.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                adapter = HomeViewPagerAdapter()
                springDotsIndicatorHome.setViewPager2(viewPagerBannerHome)
            }

            recyclerViewHome.run {
                recyclerViewHome.layoutManager = LinearLayoutManager(context)
                adapter = HomeAdapter(mainActivity, mutableListOf())
            }

            recyclerViewPromise.run {
                recyclerViewPromise.layoutManager = GridLayoutManager(context, 2)
                adapter = HomePromiseAdapter(mainActivity, mutableListOf())
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

            writeMainViewModel.checkCertification(
                accessToken,
                onSuccess = {
                    if (sortedList.isEmpty()) {
                        binding.linearLayoutNonexistenceTransactionHome.visibility = View.VISIBLE
                        binding.linearLayoutExistenceTransactionHome.visibility = View.GONE
                        binding.buttonGuideHome.visibility = View.VISIBLE
                    } else {
                        binding.linearLayoutNonexistenceTransactionHome.visibility = View.GONE
                        binding.linearLayoutExistenceTransactionHome.visibility = View.VISIBLE
                        binding.textViewIouNumberHome.text = "총 ${sortedList.size}건"
                        binding.buttonGuideHome.visibility = View.GONE
                    }
                },
                onFailure = {
                    binding.linearLayoutNonexistenceTransactionHome.visibility = View.GONE
                    binding.linearLayoutExistenceTransactionHome.visibility = View.GONE
                    binding.buttonGuideHome.visibility = View.GONE
                    binding.buttonNoCertificationHome.visibility = View.VISIBLE

                    binding.buttonNoCertificationHome.setOnClickListener {
                        startIdentityVerification()
                    }

                }
            )

        }

    }

    private fun startIdentityVerification() {

        val userCode = BuildConfig.USER_CODE
        val certification = IamPortCertification(
            merchant_uid = getRandomMerchantUid(),
            company = "멋쟁이사자처럼"
        )

        Iamport.certification(userCode, iamPortCertification = certification) {

            val userCertificationResponse = it?.imp_uid?.let { it1 -> UserCertificationResponse(it1) }

            if (it != null) {
                it.imp_uid?.let {
                    if (userCertificationResponse != null) {
                        writeMainViewModel.userCertification(accessToken, userCertificationResponse,
                            onSuccess = {
                                viewModel.viewModelScope.launch {
                                    viewModel.reloadIou(accessToken)
                                    binding.progressBarLoadingHome.visibility = View.VISIBLE
                                    binding.buttonNoCertificationHome.visibility = View.GONE
                                    delay(2000)
                                    mainActivity.replaceFragment(MainActivity.HOME_FRAGMENT, false, null)
                                }
                            }, onFailure = {
                                Log.d(TAG, "갱신 실패함")
                                mainActivity.replaceFragment(MainActivity.HOME_FRAGMENT, false, null)
                            }
                        )
                    }

                }
            }

        }
    }

    private fun getRandomMerchantUid(): String {
        return "${UUID.randomUUID()}"
    }

    private fun settingUiType(result : Boolean) {
        binding.run {

            if (result) {
                // 페이릿 클릭시
                constraintLayoutPayritHome.visibility = View.VISIBLE
                constraintLayoutPromiseHome.visibility = View.GONE
                textViewPayritTypeHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryMint))
                textViewPromiseTypeHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.grayScale06))

                viewModel.iouList.observe(viewLifecycleOwner) { iouList ->
                    Log.d(TAG, "iouList : $iouList")
                    writeMainViewModel.checkCertification(
                        accessToken,
                        onSuccess = {
                            if(iouList.isEmpty()) {
                                binding.buttonGuideHome.visibility = View.VISIBLE
                            } else {
                                binding.buttonGuideHome.visibility = View.GONE
                            }
                        },
                        onFailure = {

                        })
                }

            } else {
                // 약속 클릭시
                constraintLayoutPayritHome.visibility = View.GONE
                constraintLayoutPromiseHome.visibility = View.VISIBLE
                textViewPayritTypeHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.grayScale06))
                textViewPromiseTypeHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryMint))
                buttonGuideHome.visibility = View.GONE

                // 약속 새로 고침
                binding.refreshLayoutPromise.setOnRefreshListener {

                    promiseViewModel.getMyPromiseList(accessToken)
                    refreshLayoutPromise.isRefreshing = false
                }

                observePromiseData()
            }

        }
    }

    private fun observePromiseData() {
        promiseViewModel.myPromiseList.observe(viewLifecycleOwner) { myPromiseList ->

            (binding.recyclerViewPromise.adapter as HomePromiseAdapter).updatePromiseData(myPromiseList)

            settingPromiseCnt()
        }
    }

    private fun settingPromiseCnt() {
        val myPromiseList = (binding.recyclerViewPromise.adapter as HomePromiseAdapter).getPromiseListSize()

        if (myPromiseList == 0) {
            binding.textViewNoPromiseHome.visibility = View.VISIBLE
        } else {
            binding.textViewPromiseNumberHome.text = "총 ${myPromiseList}건"
            binding.linearLayoutExistencePromiseHome.visibility = View.VISIBLE
        }
    }

}