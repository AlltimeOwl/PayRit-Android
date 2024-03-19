package com.alltimeowl.payrit.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouBorrowDetailBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class IouBorrowDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouBorrowDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentIouBorrowDetailBinding.inflate(layoutInflater)

        initUI()

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
            buttonDocumentIouBorrowDetail.setOnClickListener {
                showBottomSheet()
            }

            // 개인 메모 클릭
            imageViewMemoIouBorrowDetail.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_DETAIL_MEMO_FRAGMENT, true, null)
            }

        }
    }

    private fun showBottomSheet() {
        val bottomSheetView = ItemDocumentBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(mainActivity)

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.background = null

            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            // PDF 다운
            bottomSheetView.linearLayoutDownloadPdfItemDocument.setOnClickListener {
                downloadPdfFromView(bottomSheetView.cardViewItemDocument)
                bottomSheetDialog.dismiss()
            }

            // 이메일 전송
            bottomSheetView.linearLayoutSendEmailItemDocument.setOnClickListener {
                sendEmailWithGmail()
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
            val pdfFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "차용증.pdf"
            )
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            Snackbar.make(binding.root, "PDF 다운로드 성공", Snackbar.LENGTH_LONG)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    private fun sendEmailWithGmail() {
        // Create an email intent
        val emailIntent = Intent(Intent.ACTION_SEND)

        // Inflate the layout containing the CardView
        val bottomSheetView = ItemDocumentBinding.inflate(layoutInflater)
        val cardView = bottomSheetView.cardViewItemDocument

        // 1단계: 레이아웃을 Bitmap으로 변환
        val bitmap = viewToBitmap(cardView)

        // 2단계: Bitmap을 PDF로 변환
        val pdfFile = createPdfFromBitmap(bitmap)

        // 3단계: 이메일에 첨부하여 보내기
        emailIntent.putExtra(Intent.EXTRA_STREAM, pdfFile)
        emailIntent.type = "application/pdf"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))

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

}