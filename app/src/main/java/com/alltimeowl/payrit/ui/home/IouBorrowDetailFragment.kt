package com.alltimeowl.payrit.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentIouBorrowDetailBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

class IouBorrowDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouBorrowDetailBinding

    private lateinit var viewModel: HomeViewModel

    private var paperId = 0
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

    val TAG = "IouBorrowDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentIouBorrowDetailBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        paperId = arguments?.getInt("paperId", paperId)!!

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getIouDetail(accessToken, paperId)

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarIouBorrowDetail.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_BORROW_DETAIL_FRAGMENT)
                }
            }

            // PDF·메일 내보내기 클릭
            linearLayoutDocumentIouBorrowDetail.setOnClickListener {
                showBottomSheet()
            }

            // 개인 메모 클릭
            linearLayoutMemoIouBorrowDetail.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("paperId", paperId)

                mainActivity.replaceFragment(MainActivity.IOU_DETAIL_MEMO_FRAGMENT, true, bundle)
            }

        }
    }

    private fun showBottomSheet() {
        val bottomSheetView = ItemDocumentBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(mainActivity)

        // 채권자 정보
        bottomSheetView.textViewIouCreditorNameItemDocument.text = creditorName
        bottomSheetView.textViewIouCreditorPhoneNumberItemDocument.text = mainActivity.formatPhoneNumber(creditorPhoneNumber)
        bottomSheetView.textViewIouCreditorAddressItemDocument.text = creditorAddress

        // 채무자 정보
        bottomSheetView.textViewIouDebtorNameItemDocument.text = debtorName
        bottomSheetView.textViewIouDebtorPhoneNumberItemDocument.text = mainActivity.formatPhoneNumber(debtorPhoneNumber)
        bottomSheetView.textViewIouDebtorAddressItemDocument.text = debtorAddress

        // 차용금액 및 변제 조건
        bottomSheetView.textViewTableAmountItemDocument.text = "원금 ${mainActivity.numberToKorean(primeAmount)}      원정 (₩ ${mainActivity.convertMoneyFormat(primeAmount)})"

        if ((interestRate <= 0.0 || interestRate > 20.00)) {
            bottomSheetView.textViewTableInterestItemDocument.text = "연 (  )%"
        } else {
            bottomSheetView.textViewTableInterestItemDocument.text = "연 ( ${interestRate} )%"
        }

        if (interestPaymentDate == 0) {
            bottomSheetView.textViewTableInterestDateItemDocument.text = "매월 ( )일에 지급"
        } else {
            bottomSheetView.textViewTableInterestDateItemDocument.text = "매월 ( ${interestPaymentDate} )일에 지급"
        }

        bottomSheetView.textViewTableRepaymentDateItemDocument.text = mainActivity.iouConvertDateFormat(repaymentEndDate)

        if (specialConditions.isNotEmpty()) {
            bottomSheetView.textViewTableConditionItemDocument.text = specialConditions
        }

        // 작성일
        bottomSheetView.textViewFinalIouDateItemDocument.text = mainActivity.iouConvertDateFormat(transactionDate)

        // 채권자
        bottomSheetView.textViewFinalIouCreditorNameItemDocument.text = "채 권 자 : ${creditorName} (인)"

        // 채무자
        bottomSheetView.textViewFinalIouDebtorNameItemDocument.text = "채 무 자 : ${debtorName} (인)"

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.background = null

            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            // PDF 다운
            bottomSheetView.linearLayoutDownloadPdfItemDocument.setOnClickListener {

                if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                } else {
                    downloadPdfFromView(bottomSheetView.cardViewItemDocument)
                }

                bottomSheetDialog.dismiss()
            }

            // 이메일 전송
            bottomSheetView.linearLayoutSendEmailItemDocument.setOnClickListener {
                sendEmailWithGmail(bottomSheetView)
                bottomSheetDialog.dismiss()
            }

            // 취소
            bottomSheetView.linearLayoutCancelItemDocument.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

        }

        bottomSheetDialog.setContentView(bottomSheetView.root)
        bottomSheetDialog.show()
    }

    // PDF 다운로드 함수
    private fun downloadPdfFromView(cardView: CardView) {
        // 1단계: 레이아웃을 Bitmap으로 변환
        val bitmap = Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        cardView.draw(canvas)

        // 2단계: Bitmap을 PDF로 변환
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val pdfCanvas = page.canvas
        pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        // PDF 저장
        try {
            // 현재 시간을 "yyyyMMdd_HHmmss" 형태의 문자열로 변환
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val fileName = "차용증_$timeStamp.pdf"

            val pdfFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName
            )
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            Snackbar.make(binding.root, "PDF 다운로드 성공", Snackbar.LENGTH_LONG)
                .show()
        } catch (e: IOException) {
            Log.e(TAG, "PDF 저장 실패: ", e)
        } catch (e: Exception) {
            Log.e(TAG, "알 수 없는 에러 발생: ", e)
        } finally {
            pdfDocument.close()
        }
    }

    private fun sendEmailWithGmail(bottomSheetView: ItemDocumentBinding) {
        // Create an email intent
        val emailIntent = Intent(Intent.ACTION_SEND)

        // Inflate the layout containing the CardView
        // val bottomSheetView = ItemDocumentBinding.inflate(layoutInflater)
        val cardView = bottomSheetView.cardViewItemDocument

        // 1단계: 레이아웃을 Bitmap으로 변환
        val bitmap = viewToBitmap(cardView)

        // 2단계: Bitmap을 PDF로 변환
        val pdfFile = createPdfFromBitmap(bitmap)

        // 3단계: 이메일에 첨부하여 보내기
        emailIntent.putExtra(Intent.EXTRA_STREAM, pdfFile)
        emailIntent.type = "application/pdf"
        // 받는 사람 이메일 필요시
        // emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))

        // Check if Gmail app is available
        emailIntent.setPackage("com.google.android.gm")
        if (emailIntent.resolveActivity(mainActivity.packageManager) != null) {
            startActivity(emailIntent)
        } else {
            // If Gmail app is not available, start email chooser
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }
    }

    // Function to convert a view to bitmap
    private fun viewToBitmap(view: View): Bitmap {
        // View의 크기를 측정하여 비트맵 생성
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val width = view.measuredWidth
        val height = view.measuredHeight

        // View의 크기가 0일 경우 예외 처리
        if (width <= 0 || height <= 0) {
            throw IllegalArgumentException("View dimensions must be > 0")
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, width, height)
        view.draw(canvas)

        return bitmap
    }

    // Function to create a PDF file from a bitmap
    private fun createPdfFromBitmap(bitmap: Bitmap): Uri {
        val pdfFile = File(requireContext().cacheDir, "차용증.pdf")
        pdfFile.createNewFile()

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)

        document.writeTo(pdfFile.outputStream())
        document.close()

        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", pdfFile)
    }

    private fun observeData() {
        viewModel.iouDetail.observe(viewLifecycleOwner) { iouDetailInfo ->
            Log.d(TAG, "iouDetailInfo : ${iouDetailInfo}")

            binding.progressBarLoadingIouBorrowDetail.visibility = View.GONE
            binding.scrollviewIouBorrowDetail.visibility = View.VISIBLE

            binding.textViewNameIouBorrowDetail.text = iouDetailInfo.creditorProfile.name + "님께"
            binding.textViewTotalAmountIouBorrowDetail.text = mainActivity.convertMoneyFormat(iouDetailInfo.paperFormInfo.amount)
            binding.textViewPeriodIouBorrowDetail.text = "원금상환일 ${mainActivity.convertDateFormat(iouDetailInfo.paperFormInfo.repaymentEndDate)}"
            binding.textViewRemainingAmountIouBorrowDetail.text = mainActivity.convertMoneyFormat(iouDetailInfo.paperFormInfo.remainingAmount) + "원"

            if (iouDetailInfo.dueDate >= 0) {
                binding.textViewDayIouBorrowDetail.text = "D - ${iouDetailInfo.dueDate}"
            } else {
                binding.textViewDayIouBorrowDetail.text = "D + ${abs(iouDetailInfo.dueDate)}"
            }

            binding.progressBarIouBorrowDetail.progress = iouDetailInfo.repaymentRate.toInt()
            binding.textViewPercentIouBorrowDetail.text = "(${iouDetailInfo.repaymentRate.toInt()}%)"

            // 메모 개수
            binding.textViewMemoCountIouBorrowDetail.text = iouDetailInfo.memoListResponses.size.toString() + "건"

            // 빌려준 사람
            binding.textViewLendPersonNameIouBorrowDetail.text = iouDetailInfo.creditorProfile.name
            binding.textViewLendPersonPhoneNumberIouBorrowDetail.text = mainActivity.formatPhoneNumber(iouDetailInfo.creditorProfile.phoneNumber)

            if (iouDetailInfo.creditorProfile.address.isEmpty()) {
                binding.linearLayoutLendPersonAddressIouBorrowDetail.visibility = View.GONE
            } else {
                binding.textViewLendPersonAddressIouBorrowDetail.text = iouDetailInfo.creditorProfile.address
            }

            // 빌린 사람
            binding.textViewBorrowPersonNameIouBorrowDetail.text = iouDetailInfo.debtorProfile.name
            binding.textViewBorrowPersonPhoneNumberIouBorrowDetail.text = mainActivity.formatPhoneNumber(iouDetailInfo.debtorProfile.phoneNumber)

            if (iouDetailInfo.debtorProfile.address.isEmpty()) {
                binding.linearLayoutBorrowPersonAddressIouBorrowDetail.visibility = View.GONE
            } else {
                binding.textViewBorrowPersonAddressIouBorrowDetail.text = iouDetailInfo.debtorProfile.address
            }

            // 추가 사항
            if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00) && iouDetailInfo.paperFormInfo.interestPaymentDate == 0 && iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                binding.cardViewAdditionInformationIouBorrowDetail.visibility = View.GONE
            } else {

                // 이자율
                if (iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00) {
                    binding.linearLayoutAdditionalInformationInterestRateIouBorrowDetail.visibility = View.GONE
                } else {
                    binding.textViewAdditionalInformationInterestRateIouBorrowDetail.text = "${iouDetailInfo.paperFormInfo.interestRate}%"
                }

                // 이자 지급일
                if (iouDetailInfo.paperFormInfo.interestPaymentDate == 0) {
                    binding.linearLayoutAdditionalInformationInterestDateIouBorrowDetail.visibility = View.GONE
                } else {
                    binding.textViewAdditionalInformationInterestDateIouBorrowDetail.text = "매월 ${iouDetailInfo.paperFormInfo.interestPaymentDate}일"
                }

                // 특약사항
                if (iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                    binding.linearLayoutAdditionalInformationIouBorrowDetail.visibility = View.GONE
                } else {
                    binding.textViewAdditionalInformationIouBorrowDetail.text = iouDetailInfo.paperFormInfo.specialConditions
                }

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

        }
    }

}