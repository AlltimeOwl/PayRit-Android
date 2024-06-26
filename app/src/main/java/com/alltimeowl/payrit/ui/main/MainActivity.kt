package com.alltimeowl.payrit.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.ActivityMainBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.databinding.ItemModifyCancelBinding
import com.alltimeowl.payrit.ui.approval.RecipientApprovalFragment
import com.alltimeowl.payrit.ui.card.CardSelectFragment
import com.alltimeowl.payrit.ui.card.CardSettingFragment
import com.alltimeowl.payrit.ui.home.HomeFragment
import com.alltimeowl.payrit.ui.home.HomePromiseInfoFragment
import com.alltimeowl.payrit.ui.home.IouBorrowDetailFragment
import com.alltimeowl.payrit.ui.home.IouDetailAmountReceivedFragment
import com.alltimeowl.payrit.ui.home.IouDetailFragment
import com.alltimeowl.payrit.ui.home.IouDetailMemoFragment
import com.alltimeowl.payrit.ui.mypage.AccountInformationFragment
import com.alltimeowl.payrit.ui.mypage.CertificationInfoFragment
import com.alltimeowl.payrit.ui.mypage.MyPageMainFragment
import com.alltimeowl.payrit.ui.mypage.NotificationSettingFragment
import com.alltimeowl.payrit.ui.mypage.PaymentHistoryDetailFragment
import com.alltimeowl.payrit.ui.mypage.PaymentHistoryFragment
import com.alltimeowl.payrit.ui.mypage.WithdrawalFragment
import com.alltimeowl.payrit.ui.payment.PaymentFragment
import com.alltimeowl.payrit.ui.promise.PromiseContactFragment
import com.alltimeowl.payrit.ui.promise.PromiseInformationFragment
import com.alltimeowl.payrit.ui.promise.PromiseMainFragment
import com.alltimeowl.payrit.ui.promise.PromiseMakeFragment
import com.alltimeowl.payrit.ui.search.SearchFragment
import com.alltimeowl.payrit.ui.write.IouContentCheckFragment
import com.alltimeowl.payrit.ui.write.IouMainFragment
import com.alltimeowl.payrit.ui.write.IouTransactionalInformationFragment
import com.alltimeowl.payrit.ui.write.IouWriteMyFragment
import com.alltimeowl.payrit.ui.write.IouWriteOpponentFragment
import com.alltimeowl.payrit.ui.write.KakaoZipCodeFragment
import com.alltimeowl.payrit.ui.write.WriteMainFragment
import com.alltimeowl.payrit.ui.writer.IouWriterApprovalWaitingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    private var lastBackPressedTime: Long = 0
    private val BACK_PRESS_INTERVAL: Long = 2000

    val TAG = "MainActivity1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.bottomNavigationViewMain.itemIconTintList = null

        replaceFragment(HOME_FRAGMENT, false, null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // 인텐트에서 데이터 가져오기
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        Log.d(TAG, "data : ${data}")

        if (action == Intent.ACTION_VIEW && data != null) {
            // 쿼리 파라미터에서 promiseId 가져오기
            val promiseId = data.getQueryParameter("promiseId")

            if (promiseId != null) {
                // promiseId가 존재하는 경우
                Log.d(TAG, "promiseId : $promiseId")
                // 필요한 API 호출 또는 다른 작업 수행

                val bundle = Bundle()
                bundle.putString("promiseId", promiseId)

                replaceFragment(HOME_FRAGMENT, false, bundle)
            } else {
                Log.d(TAG, "promiseId 가 존재 하지 않을 때 data : ${data}")
            }
        }
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
            RECIPIENT_APPROVAL_FRAGMENT -> RecipientApprovalFragment()
            SEARCH_FRAGMENT -> SearchFragment()
            PAYMENT_FRAGMENT -> PaymentFragment()
            CERTIFICATION_INFO_FRAGMENT -> CertificationInfoFragment()
            IOU_WRITER_APPROVAL_WAITING_FRAGMENT -> IouWriterApprovalWaitingFragment()
            PROMISE_MAIN_FRAGMENT -> PromiseMainFragment()
            PROMISE_CONTACT_FRAGMENT -> PromiseContactFragment()
            PROMISE_INFORMATION_FRAGMENT -> PromiseInformationFragment()
            PROMISE_MAKE_FRAGMENT -> PromiseMakeFragment()
            HOME_PROMISE_INFO_FRAGMENT -> HomePromiseInfoFragment()
            CARD_SETTING_FRAGMENT -> CardSettingFragment()
            CARD_SELECT_FRAGMENT -> CardSelectFragment()

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

    fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun formatPhoneNumber(phoneNumber: String): String {
        // 정규식을 사용하여 입력된 전화번호에서 숫자만 추출합니다.
        val digits = phoneNumber.filter { it.isDigit() }

        // 숫자만 추출된 전화번호를 원하는 형식으로 변환합니다.
        return if (digits.length == 11) {
            "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7, 11)}"
        } else {
            // 입력된 전화번호의 길이가 11자리가 아닌 경우, 원본 전화번호를 반환합니다.
            phoneNumber
        }
    }

    fun convertPhoneNumber(phoneNumber : String): String {
        val processedPhoneNumber = phoneNumber.replace(Regex("^\\+\\d+\\s"), "")

        return "0$processedPhoneNumber"
    }

    fun convertToInternationalFormat(phoneNumber: String): String {
        val localNumber = phoneNumber.removePrefix("010")

        return "+82 10$localNumber"
    }

    fun convertDateFormat(inputDate : String) : String {

        // 원본 날짜 형식
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 변환하고자 하는 날짜 형식
        val targetFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        // Date 객체로 파싱
        val date = originalFormat.parse(inputDate)

        // 파싱된 Date 객체를 새로운 형식으로 포매팅
        val formattedDate = if (date != null) targetFormat.format(date) else ""

        return formattedDate
    }

    fun iouConvertDateFormat(inputDate : String) : String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy 년 MM월 dd일", Locale.getDefault())

        val date = originalFormat.parse(inputDate)

        val formattedDate = if (date != null) targetFormat.format(date) else ""

        return formattedDate
    }

    fun convertMoneyFormat(inputMoney : Int) : String{
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        return formatter.format(inputMoney)
    }

    fun numberToKorean(number: Int): String {
        val numberUnits = arrayOf("", "만", "억", "조")
        val digitUnits = arrayOf("", "십", "백", "천")
        val koreanNumbers = arrayOf("", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구")

        if (number == 0) {
            return "영"
        }

        var result = ""
        var num = number
        var unitIndex = 0

        while (num > 0) {
            val part = num % 10000 // 만 단위로 분리
            var partResult = ""
            var partNum = part
            var digitIndex = 0

            while (partNum > 0) {
                val digit = partNum % 10 // 각 자리수 분리
                if (digit > 0) {
                    partResult = koreanNumbers[digit] + digitUnits[digitIndex] + partResult
                }
                partNum /= 10
                digitIndex += 1
            }

            if (part > 0) {
                result = partResult + numberUnits[unitIndex] + result
            }
            num /= 10000
            unitIndex += 1
        }

        return result + "원"
    }

    fun showModifyAlertDialog() {
        val itemModifyCancelBinding = ItemModifyCancelBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(itemModifyCancelBinding.root)
        val dialog = builder.create()

        // 작성 중단 - 네
        itemModifyCancelBinding.textViewYesModifyCancel.setOnClickListener {
            dialog.dismiss()

            removeAllBackStack()
        }

        // 작성 중단 - 아니오
        itemModifyCancelBinding.textViewNoModifyCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun extractAddressComponents(fullAddress: String): Pair<String, String>? {
        // 주소 형식: "전체주소 (우편번호)"
        val regex = """^(.+?)\s+\((\d+)\)$""".toRegex()
        val matchResult = regex.find(fullAddress)

        return matchResult?.let {
            val (address, postalCode) = it.destructured
            Pair(address, postalCode)
        }
    }

    fun showCancelAlertDialog() {
        val itemCancelBinding = ItemCancelBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(itemCancelBinding.root)
        val dialog = builder.create()

        // 작성 중단 - 네
        itemCancelBinding.textViewYesCancel.setOnClickListener {
            dialog.dismiss()

            removeAllBackStack()
        }

        // 작성 중단 - 아니오
        itemCancelBinding.textViewNoCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
        const val RECIPIENT_APPROVAL_FRAGMENT = "RecipientApprovalFragment"
        const val SEARCH_FRAGMENT = "SearchFragment"
        const val PAYMENT_FRAGMENT = "PaymentFragment"
        const val CERTIFICATION_INFO_FRAGMENT = "CertificationInfoFragment"
        const val IOU_WRITER_APPROVAL_WAITING_FRAGMENT = "IouWriterApprovalWaitingFragment"
        const val PROMISE_MAIN_FRAGMENT = "PromiseMainFragment"
        const val PROMISE_CONTACT_FRAGMENT = "PromiseContactFragment"
        const val PROMISE_INFORMATION_FRAGMENT = "PromiseInformationFragment"
        const val PROMISE_MAKE_FRAGMENT = "PromiseMakeFragment"
        const val HOME_PROMISE_INFO_FRAGMENT = "HomePromiseInfoFragment"
        const val CARD_SETTING_FRAGMENT = "CardSettingFragment"
        const val CARD_SELECT_FRAGMENT = "CardSelectFragment"
    }
}