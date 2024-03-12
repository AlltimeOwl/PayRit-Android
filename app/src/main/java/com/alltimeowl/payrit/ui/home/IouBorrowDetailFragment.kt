package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouBorrowDetailBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

            // 확대 클릭
            imageViewEnlargeIouBorrowDetail.setOnClickListener {
                showAlertDialog()
            }

            // 개인 메모 클릭
            imageViewMemoIouBorrowDetail.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_DETAIL_MEMO_FRAGMENT, true, null)
            }

        }
    }

    private fun showAlertDialog() {
        val itemDocumentBinding = ItemDocumentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemDocumentBinding.root)
        val dialog = builder.create()

        dialog.show()
    }

}