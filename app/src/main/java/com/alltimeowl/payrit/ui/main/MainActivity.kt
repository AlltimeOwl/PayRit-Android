package com.alltimeowl.payrit.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.ActivityMainBinding
import com.alltimeowl.payrit.ui.home.HomeFragment
import com.alltimeowl.payrit.ui.mypage.AccountInformationFragment
import com.alltimeowl.payrit.ui.mypage.MyPageMainFragment
import com.alltimeowl.payrit.ui.mypage.NotificationSettingFragment
import com.alltimeowl.payrit.ui.mypage.WithdrawalFragment
import com.alltimeowl.payrit.ui.write.IouContentCheckFragment
import com.alltimeowl.payrit.ui.write.IouMainFragment
import com.alltimeowl.payrit.ui.write.IouTransactionalInformationFragment
import com.alltimeowl.payrit.ui.write.IouWriteMyFragment
import com.alltimeowl.payrit.ui.write.IouWriteOpponentFragment
import com.alltimeowl.payrit.ui.write.KakaoZipCodeFragment
import com.alltimeowl.payrit.ui.write.WriteMainFragment

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        replaceFragment(HOME_FRAGMENT, false, null)
    }

    fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle?){

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var newFragment: Fragment? = null

        newFragment = when(name){
            HOME_FRAGMENT -> HomeFragment()
            WRITE_MAIN_FRAGMENT -> WriteMainFragment()
            IOU_MAIN_FRAGMENT -> IouMainFragment()
            MY_PAGE_MAIN_FRAGMENT -> MyPageMainFragment()
            ACCOUNT_INFORMATION_FRAGMENT -> AccountInformationFragment()
            WITHDRAWAL_FRAGMENT -> WithdrawalFragment()
            NOTIFICATION_SETTING_FRAGMENT -> NotificationSettingFragment()
            IOU_TRANSACTIONAL_INFORMATION_FRAGMENT -> IouTransactionalInformationFragment()
            IOU_WRITE_MY_FRAGMENT -> IouWriteMyFragment()
            KAKAO_ZIP_CODE_FRAGMENT -> KakaoZipCodeFragment()
            IOU_WRITE_OPPONENT_FRAGMENT -> IouWriteOpponentFragment()
            IOU_CONTENT_CHECK_FRAGMENT -> IouContentCheckFragment()

            else -> Fragment()
        }

        newFragment?.arguments = bundle

        if(newFragment != null) {

            fragmentTransaction.replace(R.id.fragmentContainerView_main, newFragment)

            if (addToBackStack == true) {
                fragmentTransaction.addToBackStack(name)
            }

            fragmentTransaction.commit()
        }
    }

    fun removeFragment(name: String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun showBottomNavigationView() {
        activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }

    fun hideBottomNavigationView() {
        activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
    }

    fun bottomNavigation() {

        activityMainBinding.bottomNavigationViewMain.run {

            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.home_menu -> {
                        replaceFragment(HOME_FRAGMENT, false, null)
                        return@setOnItemSelectedListener true
                    }
                    R.id.write_menu -> {
                        replaceFragment(WRITE_MAIN_FRAGMENT, false, null)
                        return@setOnItemSelectedListener true
                    }
                    R.id.my_menu -> {
                        replaceFragment(MY_PAGE_MAIN_FRAGMENT, false, null)
                        return@setOnItemSelectedListener true
                    }

                    else -> return@setOnItemSelectedListener false
                }
            }
        }
    }

    fun removeAllBackStack() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }


    companion object {

        const val HOME_FRAGMENT = "HomeFragment"
        const val WRITE_MAIN_FRAGMENT = "WriteMainFragment"
        const val MY_PAGE_MAIN_FRAGMENT = "MyPageMainFragment"
        const val ACCOUNT_INFORMATION_FRAGMENT = "AccountInformationFragment"
        const val WITHDRAWAL_FRAGMENT = "WithdrawalFragment"
        const val NOTIFICATION_SETTING_FRAGMENT = "NotificationSettingFragment"
        const val IOU_MAIN_FRAGMENT = "IouMainFragment"
        const val IOU_TRANSACTIONAL_INFORMATION_FRAGMENT = "IouTransactionalInformationFragment"
        const val IOU_WRITE_MY_FRAGMENT = "IouWriteMyFragment"
        const val KAKAO_ZIP_CODE_FRAGMENT = "KakaoZipCodeFragment"
        const val IOU_WRITE_OPPONENT_FRAGMENT = "IouWriteOpponentFragment"
        const val IOU_CONTENT_CHECK_FRAGMENT = "IouContentCheckFragment"
    }
}