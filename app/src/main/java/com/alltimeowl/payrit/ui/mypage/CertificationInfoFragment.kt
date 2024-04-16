package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentCertificationInfoBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.alltimeowl.payrit.ui.write.WriteMainViewModel

class CertificationInfoFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentCertificationInfoBinding

    lateinit var viewModel: WriteMainViewModel

    val TAG = "CertificationInfoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentCertificationInfoBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[WriteMainViewModel::class.java]

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getCertificationInfo(accessToken)

        initUi()
        observeData()

        return binding.root
    }

    private fun initUi() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarCertificationInfo.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CERTIFICATION_INFO_FRAGMENT)
                }
            }

        }
    }

    private fun observeData() {
        viewModel.certificationUserInfo.observe(viewLifecycleOwner) { certificationInfoResponse ->
            binding.buttonUserNameCertificationInfo.text = certificationInfoResponse.certificationName
            binding.buttonUserGenderCertificationInfo.text = certificationInfoResponse.certificationGender
            binding.buttonUserBirthdayCertificationInfo.text = certificationInfoResponse.certificationBirthday
            binding.buttonUserPhoneNumberCertificationInfo.text = mainActivity.formatPhoneNumber(certificationInfoResponse.certificationPhoneNumber)
        }
    }

}