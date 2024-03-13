package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
                    } else if (iou.type == "빌린 돈" && iou.state == "상환 진행중") {
                        mainActivity.replaceFragment(MainActivity.IOU_BORROW_DETAIL_FRAGMENT, true, null)
                    }
                }
            }
        }

        fun bind(iou: sampleIou) {
            binding.textViewPeriodIou.text = iou.period
            binding.textViewTypeIou.text = iou.type
            binding.textViewTotalAmountIou.text = "${iou.amount}원"
            binding.textViewNameIou.text = iou.name
            binding.textViewDayIou.text = iou.day
            binding.textViewStateIou.text = iou.state

            // 차용증 타입 (빌려준 돈, 빌린 돈)에 따른 글자 색상, ProgressBar 색상 변경
            when (iou.type) {
                "빌려준 돈" -> {
                    binding.textViewTypeIou.setTextColor(ContextCompat.getColor(mainActivity, R.color.primaryMint))
                    binding.progressBarIou.progressDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.bg_progress_bar_mint)
                }
                "빌린 돈" -> {
                    binding.textViewTypeIou.setTextColor(ContextCompat.getColor(mainActivity, R.color.pink))
                    binding.progressBarIou.progressDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.bg_progress_bar_pink)
                }
            }

            // Repay와 Amount를 정수로 변환하여 계산하고, 그 결과를 문자열로 변환하여 TextView에 설정
            val repay = iou.repay.replace(",", "").toDoubleOrNull() ?: 0.0
            val amount = iou.amount.replace(",", "").toDoubleOrNull() ?: 0.0
            val percent = (repay / amount) * 100
            val formattedPercent = String.format("%d%%", percent.toInt())

            binding.textViewPercentIou.text = "(${formattedPercent})"
            binding.progressBarIou.progress = percent.toInt()
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