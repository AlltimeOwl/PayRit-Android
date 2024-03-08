package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouDetailBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IouDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouDetailBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarIouDetail.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_DETAIL_FRAGMENT)
                }
            }

            // 확대 클릭
            imageViewEnlargeIouDetail.setOnClickListener {
                showAlertDialog()
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