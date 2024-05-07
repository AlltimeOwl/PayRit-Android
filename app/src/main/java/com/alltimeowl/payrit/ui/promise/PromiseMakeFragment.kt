package com.alltimeowl.payrit.ui.promise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentPromiseMakeBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class PromiseMakeFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseMakeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseMakeBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            materialToolbarPromiseMake.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_MAKE_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> mainActivity.showCancelAlertDialog()
                    }
                    false
                }
            }

            recyclerViewPromiseMake.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = PromiseMakeAdapter(binding)
            }

        }
    }

}