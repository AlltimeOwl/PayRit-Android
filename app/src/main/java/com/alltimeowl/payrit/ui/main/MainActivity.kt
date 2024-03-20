package com.alltimeowl.payrit.ui.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.ActivityMainBinding
import com.alltimeowl.payrit.ui.home.HomeFragment
import com.alltimeowl.payrit.ui.home.IouBorrowDetailFragment
import com.alltimeowl.payrit.ui.home.IouDetailAmountReceivedFragment
import com.alltimeowl.payrit.ui.home.IouDetailFragment
import com.alltimeowl.payrit.ui.home.IouDetailMemoFragment
import com.alltimeowl.payrit.ui.mypage.AccountInformationFragment
import com.alltimeowl.payrit.ui.mypage.MyPageMainFragment
import com.alltimeowl.payrit.ui.mypage.NotificationSettingFragment
import com.alltimeowl.payrit.ui.mypage.PaymentHistoryDetailFragment
import com.alltimeowl.payrit.ui.mypage.PaymentHistoryFragment
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

    private var lastBackPressedTime: Long = 0
    private val BACK_PRESS_INTERVAL: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.bottomNavigationViewMain.itemIconTintList = null

        replaceFragment(HOME_FRAGMENT, false, null)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main)

        when (currentFragment) {
            is HomeFragment, is WriteMainFragment, is MyPageMainFragment -> handleBackPressed()
            else -> super.onBackPressed()
        }
    }

    private fun handleBackPressed() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBackPressedTime > BACK_PRESS_INTERVAL) {
            lastBackPressedTime = currentTime

            Toast.makeText(this, "뒤로 가기를 한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        } else {
            // 해당 Fragment에서 두 번의 뒤로 가기 버튼이 BACK_PRESS_INTERVAL 안에 눌렸으므로 앱을 종료
            finish()
        }
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
            IOU_DETAIL_FRAGMENT -> IouDetailFragment()
            IOU_DETAIL_AMOUNT_RECEIVED_FRAGMENT -> IouDetailAmountReceivedFragment()
            IOU_DETAIL_MEMO_FRAGMENT -> IouDetailMemoFragment()
            IOU_BORROW_DETAIL_FRAGMENT -> IouBorrowDetailFragment()
            PAYMENT_HISTORY_FRAGMENT -> PaymentHistoryFragment()
            PAYMENT_HISTORY_DETAIL_FRAGMENT -> PaymentHistoryDetailFragment()

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

    fun selectBottomNavigationItem(itemId: Int) {
        activityMainBinding.bottomNavigationViewMain.selectedItemId = itemId
    }

    fun showBottomNavigationView() {
        activityMainBinding.cardViewMain.visibility = View.VISIBLE
    }

    fun hideBottomNavigationView() {
        activityMainBinding.cardViewMain.visibility = View.GONE
    }

    fun bottomNavigation() {

        activityMainBinding.bottomNavigationViewMain.run {

            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.home_menu -> {
                        removeAllBackStack()
                        replaceFragment(HOME_FRAGMENT, false, null)
                        return@setOnItemSelectedListener true
                    }
                    R.id.write_menu -> {
                        removeAllBackStack()
                        replaceFragment(WRITE_MAIN_FRAGMENT, false, null)
                        return@setOnItemSelectedListener true
                    }
                    R.id.my_menu -> {
                        removeAllBackStack()
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

    fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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
        const val IOU_DETAIL_FRAGMENT = "IouDetailFragment"
        const val IOU_DETAIL_AMOUNT_RECEIVED_FRAGMENT = "IouDetailAmountReceivedFragment"
        const val IOU_DETAIL_MEMO_FRAGMENT = "IouDetailMemoFragment"
        const val IOU_BORROW_DETAIL_FRAGMENT = "IouBorrowDetailFragment"
        const val PAYMENT_HISTORY_FRAGMENT = "PaymentHistoryFragment"
        const val PAYMENT_HISTORY_DETAIL_FRAGMENT = "PaymentHistoryDetailFragment"
    }
}