package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.sampleIou
import com.alltimeowl.payrit.databinding.ItemIouBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class HomeAdapter(val mainActivity: MainActivity, val sampleIouList: MutableList<sampleIou>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: ItemIouBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val iou = sampleIouList[position]
                    if (iou.state == "차용증 작성 완료") {
                        mainActivity.replaceFragment(MainActivity.IOU_DETAIL_FRAGMENT, true, null)
                    }
                }
            }
        }

        fun bind(iou: sampleIou) {
            binding.textViewPeriodIou.text = iou.period
            binding.textViewTotalAmountIou.text = iou.amount
            binding.textViewNameIou.text = iou.name
            binding.buttonDayIou.text = iou.day

            // 차용증 상태에 따른 배경 변경
            when (iou.state) {
                "차용증 작성 완료" -> {
                    binding.buttonStateIou.setBackgroundResource(R.drawable.bg_semicircle_iou_state_complete_r16)
                }
                "기간 만료" -> {
                    binding.buttonStateIou.setBackgroundResource(R.drawable.bg_semicircle_iou_state_expiration_r16)
                }
                else -> {
                    binding.buttonStateIou.setBackgroundResource(R.drawable.bg_semicircle_iou_state_r16)
                }
            }

            binding.buttonStateIou.text = iou.state
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val binding = ItemIouBinding.inflate(LayoutInflater.from(parent.context))
        val homeViewHolder = HomeViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return homeViewHolder

    }

    override fun getItemCount(): Int {
        return sampleIouList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(sampleIouList[position])
    }

}