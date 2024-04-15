package com.alltimeowl.payrit.ui.approval

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentRecipientApprovalBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.main.MainActivity
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class RecipientApprovalFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentRecipientApprovalBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var recipientApprovalViewModel: RecipientApprovalViewModel

    private var paperId: Int = 0
    private var buttonClickable = false

    private var creditorName = ""
    private var creditorPhoneNumber = ""
    private var creditorAddress = ""
    private var debtorName = ""
    private var debtorPhoneNumber = ""
    private var debtorAddress = ""
    private var primeAmount = 0
    private var interestRate = 0.0f
    private var interestPaymentDate = 0
    private var repaymentEndDate = ""
    private var specialConditions = ""
    private var transactionDate = ""

    val TAG = "RecipientApprovalFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentRecipientApprovalBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        recipientApprovalViewModel = ViewModelProvider(this)[RecipientApprovalViewModel::class.java]

        paperId = arguments?.getInt("paperId")!!

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getIouDetail(accessToken, paperId)

        initUI()
        observeData()
        checkBoxState()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.hideBottomNavigationView()

            materialToolbarRecipientApproval.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.RECIPIENT_APPROVAL_FRAGMENT)
                }
            }

        }
    }

    private fun observeData() {
        viewModel.iouDetail.observe(viewLifecycleOwner) { iouDetailInfo ->

            Log.d(TAG, "iouDetailInfo : ${iouDetailInfo}")

            binding.progressBarRecipientApproval.visibility = View.GONE
            binding.scrollViewRecipientApproval.visibility = View.VISIBLE

            // 거래 내역
            binding.textViewTransactionAmountRecipientApproval.text = mainActivity.convertMoneyFormat(iouDetailInfo.paperFormInfo.primeAmount) + "원"
            binding.textViewTransactionDateRecipientApproval.text = mainActivity.convertDateFormat(iouDetailInfo.paperFormInfo.repaymentEndDate)

            // 추가 사항
            if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00) && iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                binding.cardViewAdditionContractRecipientApproval.visibility = View.GONE
            } else {

                // 이자율
                if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00)) {
                    binding.linearLayoutAdditionContractInterestRateRecipientApproval.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractInterestRateRecipientApproval.text = "${iouDetailInfo.paperFormInfo.interestRate}%"
                }

                // 이자 지급일
                if (iouDetailInfo.paperFormInfo.interestPaymentDate == 0) {
                    binding.linearLayoutAdditionContractDateRecipientApproval.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractDateRecipientApproval.text = "매월 ${iouDetailInfo.paperFormInfo.interestPaymentDate}일"
                }

                // 특약사항
                if (iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                    binding.linearLayoutAdditionContractRecipientApproval.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractRecipientApproval.text = iouDetailInfo.paperFormInfo.specialConditions
                }

            }

            // 빌려준 사람
            binding.textViewLendPersonNameRecipientApproval.text = iouDetailInfo.creditorProfile.name
            binding.textViewLendPersonPhoneNumberRecipientApproval.text = mainActivity.formatPhoneNumber(iouDetailInfo.creditorProfile.phoneNumber)
            if (iouDetailInfo.creditorProfile.address.isEmpty()) {
                binding.linearLayoutLendPersonAddressRecipientApproval.visibility = View.GONE
            } else {
                binding.textViewLendPersonAddressRecipientApproval.text = iouDetailInfo.creditorProfile.address
            }

            // 빌린 사람
            binding.textViewBorrowPersonNameRecipientApproval.text = iouDetailInfo.debtorProfile.name
            binding.textViewBorrowPersonPhoneNumberRecipientApproval.text = mainActivity.formatPhoneNumber(iouDetailInfo.debtorProfile.phoneNumber)
            if (iouDetailInfo.debtorProfile.address.isEmpty()) {
                binding.linearLayoutBorrowPersonAddressRecipientApproval.visibility = View.GONE
            } else {
                binding.textViewBorrowPersonAddressRecipientApproval.text = iouDetailInfo.debtorProfile.address
            }

            creditorName = iouDetailInfo.creditorProfile.name
            creditorPhoneNumber = iouDetailInfo.creditorProfile.phoneNumber
            creditorAddress = iouDetailInfo.creditorProfile.address
            debtorName = iouDetailInfo.debtorProfile.name
            debtorPhoneNumber = iouDetailInfo.debtorProfile.phoneNumber
            debtorAddress = iouDetailInfo.debtorProfile.address
            primeAmount = iouDetailInfo.paperFormInfo.primeAmount
            interestRate = iouDetailInfo.paperFormInfo.interestRate
            interestPaymentDate = iouDetailInfo.paperFormInfo.interestPaymentDate
            repaymentEndDate = iouDetailInfo.paperFormInfo.repaymentEndDate
            specialConditions = iouDetailInfo.paperFormInfo.specialConditions
            transactionDate = iouDetailInfo.paperFormInfo.transactionDate

            iouFile()

        }
    }

    private fun checkBoxState() {

        binding.checkBoxRecipientApproval.setOnClickListener {

            buttonClickable = if (binding.checkBoxRecipientApproval.isChecked) {
                binding.buttonRecipientApproval.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                true
            } else {
                binding.buttonRecipientApproval.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                false
            }
        }

    }

    private fun iouFile() {
        val itemDocumentBinding = ItemDocumentBinding.inflate(layoutInflater)

        // 채권자 정보
        itemDocumentBinding.textViewIouCreditorNameItemDocument.text = creditorName
        itemDocumentBinding.textViewIouCreditorPhoneNumberItemDocument.text = mainActivity.formatPhoneNumber(creditorPhoneNumber)
        itemDocumentBinding.textViewIouCreditorAddressItemDocument.text = creditorAddress

        // 채무자 정보
        itemDocumentBinding.textViewIouDebtorNameItemDocument.text = debtorName
        itemDocumentBinding.textViewIouDebtorPhoneNumberItemDocument.text = mainActivity.formatPhoneNumber(debtorPhoneNumber)
        itemDocumentBinding.textViewIouDebtorAddressItemDocument.text = debtorAddress

        // 차용금액 및 변제 조건
        itemDocumentBinding.textViewTableAmountItemDocument.text = "원금 ${mainActivity.numberToKorean(primeAmount)}      원정 (₩ ${mainActivity.convertMoneyFormat(primeAmount)})"

        if ((interestRate <= 0.0 || interestRate > 20.00)) {
            itemDocumentBinding.textViewTableInterestItemDocument.text = "연 (  )%"
        } else {
            itemDocumentBinding.textViewTableInterestItemDocument.text = "연 ( ${interestRate} )%"
        }

        if (interestPaymentDate == 0) {
            itemDocumentBinding.textViewTableInterestDateItemDocument.text = "매월 ( )일에 지급"
        } else {
            itemDocumentBinding.textViewTableInterestDateItemDocument.text = "매월 ( ${interestPaymentDate} )일에 지급"
        }

        itemDocumentBinding.textViewTableRepaymentDateItemDocument.text = mainActivity.iouConvertDateFormat(repaymentEndDate)

        if (specialConditions.isNotEmpty()) {
            itemDocumentBinding.textViewTableConditionItemDocument.text = specialConditions
        }

        // 작성일
        itemDocumentBinding.textViewFinalIouDateItemDocument.text = mainActivity.iouConvertDateFormat(transactionDate)

        // 채권자
        itemDocumentBinding.textViewFinalIouCreditorNameItemDocument.text = "채 권 자 : ${creditorName} (인)"

        // 채무자
        itemDocumentBinding.textViewFinalIouDebtorNameItemDocument.text = "채 무 자 : ${debtorName} (인)"

        downloadPdfFromView(itemDocumentBinding.cardViewItemDocument)
    }

    private fun viewToBitmap(view: View): Bitmap {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun downloadPdfFromView(cardView: CardView) {
        val bitmap = viewToBitmap(cardView)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        // PDF를 저장
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"document.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument.close()

        // 파일을 MultipartBody.Part로 변환
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        approvalIou(body)
    }

    private fun approvalIou(body: MultipartBody.Part) {

        binding.buttonRecipientApproval.setOnClickListener {
            if (buttonClickable) {
                val accessToken = SharedPreferencesManager.getAccessToken()
                recipientApprovalViewModel.approvalIou(accessToken, paperId, body)
                mainActivity.removeFragment(MainActivity.RECIPIENT_APPROVAL_FRAGMENT)
            }
        }

    }

}