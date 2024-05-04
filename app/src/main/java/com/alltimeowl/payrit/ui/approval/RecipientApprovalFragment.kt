package com.alltimeowl.payrit.ui.approval

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.ModifyRequest
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentRecipientApprovalBinding
import com.alltimeowl.payrit.databinding.ItemAskRefuseBinding
import com.alltimeowl.payrit.databinding.ItemCompleteModifyBinding
import com.alltimeowl.payrit.databinding.ItemCompleteRefuseBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.databinding.ItemFailureModifyBinding
import com.alltimeowl.payrit.databinding.ItemFailureRefuseBinding
import com.alltimeowl.payrit.databinding.ItemModifyRequestBinding
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
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
    private var accessToken = ""

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

        accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getIouDetail(accessToken, paperId)

        initUI()
        observeData()
        checkBoxState()
        modifyRequestButton()
        refuseButton()

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

            if (binding.checkBoxRecipientApproval.isChecked) {
                binding.buttonModifyRequestRecipientApproval.visibility = View.GONE
                binding.linearLayoutRecipientApproval.visibility = View.VISIBLE
            } else {
                binding.buttonModifyRequestRecipientApproval.visibility = View.VISIBLE
                binding.linearLayoutRecipientApproval.visibility = View.GONE
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

        // ByteArrayOutputStream을 사용하여 PDF를 메모리에 저장
        val outputStream = ByteArrayOutputStream()
        try {
            pdfDocument.writeTo(outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val bytes = outputStream.toByteArray()
        pdfDocument.close()

        // 바이트 배열을 RequestBody로 변환
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), bytes)
        val body = MultipartBody.Part.createFormData("file", "document.pdf", requestFile)

        approvalIou(body)
    }

    private fun approvalIou(body: MultipartBody.Part) {

        binding.buttonRecipientApproval.setOnClickListener {
            recipientApprovalViewModel.approvalIou(accessToken, paperId, body)
            mainActivity.removeFragment(MainActivity.RECIPIENT_APPROVAL_FRAGMENT)
        }

    }

    // 수정 요청하기 버튼
    private fun modifyRequestButton() {
        binding.buttonModifyRequestRecipientApproval.setOnClickListener {
            val itemModifyRequestBinding = ItemModifyRequestBinding.inflate(layoutInflater)
            val builder = MaterialAlertDialogBuilder(mainActivity)
            builder.setView(itemModifyRequestBinding.root)
            val dialog = builder.create()

            var modifyRequestContents = ""

            itemModifyRequestBinding.editTextModifyRequest.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    modifyRequestContents = s.toString()

                    if (modifyRequestContents.isEmpty()) {
                        itemModifyRequestBinding.textViewHintModifyRequest.visibility = View.VISIBLE
                    } else {
                        itemModifyRequestBinding.textViewHintModifyRequest.visibility = View.GONE
                    }
                }

            })

            // 수정사항 요청 - 취소
            itemModifyRequestBinding.textViewCancelModifyRequest.setOnClickListener {
                dialog.dismiss()
            }

            // 수정사항 요청 - 보내기
            itemModifyRequestBinding.textViewSendModifyRequest.setOnClickListener {

                val modifyRequest = ModifyRequest(paperId, modifyRequestContents)
                recipientApprovalViewModel.modifyIouRequest(accessToken, modifyRequest,
                    onSuccess = {
                        showCompleteAlertDialog()
                    }, onFailure = {
                        showFailureAlertDialog()
                    }
                )

                dialog.dismiss()
            }

            dialog.show()
        }
    }

    // 수정 요청 성공
    private fun showCompleteAlertDialog() {
        val itemCompleteModifyBinding = ItemCompleteModifyBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemCompleteModifyBinding.root)
        val dialog = builder.create()

        itemCompleteModifyBinding.textViewCheckCompleteModify.setOnClickListener {
            mainActivity.removeFragment(MainActivity.RECIPIENT_APPROVAL_FRAGMENT)
            dialog.dismiss()
        }

        dialog.show()
    }

    // 수정 요청 실패
    private fun showFailureAlertDialog() {
        val itemFailureModifyBinding = ItemFailureModifyBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemFailureModifyBinding.root)
        val dialog = builder.create()

        itemFailureModifyBinding.textViewCheckFailureModify.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 거절 버튼 클릭
    private fun refuseButton() {
        binding.buttonRefuseRecipientApproval.setOnClickListener {
            val itemAskRefuseBinding = ItemAskRefuseBinding.inflate(layoutInflater)
            val builder = MaterialAlertDialogBuilder(mainActivity)
            builder.setView(itemAskRefuseBinding.root)
            val dialog = builder.create()

            itemAskRefuseBinding.textViewYesAskRefuse.setOnClickListener {
                recipientApprovalViewModel.refuseIou(accessToken, paperId,
                    onSuccess = {
                        completeRefuseAlertDialog()
                    }, onFailure = {
                        failureRefuseAlertDialog()
                    }
                )
                dialog.dismiss()
            }

            itemAskRefuseBinding.textViewNoAskRefuse.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun completeRefuseAlertDialog() {
        val itemCompleteRefuseBinding = ItemCompleteRefuseBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemCompleteRefuseBinding.root)
        val dialog = builder.create()

        itemCompleteRefuseBinding.textViewCompleteRefuse.setOnClickListener {
            mainActivity.removeAllBackStack()
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun failureRefuseAlertDialog() {
        val itemFailureRefuseBinding = ItemFailureRefuseBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemFailureRefuseBinding.root)
        val dialog = builder.create()

        itemFailureRefuseBinding.textViewCheckFailureRefuse.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}