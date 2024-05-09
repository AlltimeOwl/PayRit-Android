package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.PromiseDetail
import com.alltimeowl.payrit.databinding.ItemHomePromiseCardBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.text.NumberFormat
import java.util.Locale

class HomePromiseAdapter(val mainActivity: MainActivity, var myPromiseList: MutableList<PromiseDetail>) : RecyclerView.Adapter<HomePromiseAdapter.HomePromiseViewHolder>() {

    inner class HomePromiseViewHolder(private val binding: ItemHomePromiseCardBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // ImageView 클릭 리스너 설정
            binding.imageViewHomePromiseCard.setOnClickListener {
                val isCurrentlyVisible = binding.frameLayoutHomePromiseCard.visibility == View.VISIBLE

                if (isCurrentlyVisible) {
                    // TextView를 숨기고 ImageView의 투명도를 원래대로 설정
                    binding.frameLayoutHomePromiseCard.visibility = View.GONE
                    binding.imageViewHomePromiseCard.alpha = 1.0f // 완전 불투명
                } else {
                    // TextView를 보여주고 ImageView를 흐리게 함
                    binding.frameLayoutHomePromiseCard.visibility = View.VISIBLE
                    binding.imageViewHomePromiseCard.alpha = 0.5f // 투명도 조절로 흐리게 표시

                    // 내용 보기 클릭시
                    binding.buttonDetailHomePromiseCard.setOnClickListener {

                        val position = adapterPosition
                        val promise = myPromiseList[position]

                        Log.d("HomePromiseAdapter", "클릭된 promise 정보 : $promise")

                        val bundle = Bundle()
                        bundle.putParcelable("promiseDetailInfo", promise)
                        Log.d("HomePromiseAdapter", "bundle : $bundle")

                        mainActivity.replaceFragment(MainActivity.HOME_PROMISE_INFO_FRAGMENT, true, bundle)
                    }

                }
            }
        }

        fun bind(promise: PromiseDetail) {

            // 작성자
            binding.textViewMyInfoHomePromiseCard.text = promise.writerName + "님과"

            // 받는 사람
            if (promise.participants.size == 1) {
                binding.textViewOpponentInfoHomePromiseCard.text = promise.participants[0].participantsName + "님의 약속"
            } else {
                binding.textViewOpponentInfoHomePromiseCard.text = promise.participants[0].participantsName + "님 외 ${promise.participants.size - 1}명의 약속"
            }

            // 금액
            val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(promise.amount)
            binding.textViewAmountHomePromiseCard.text = formattedAmount + "원"

            // 사진
            val imageResource = when(promise.promiseImageType) {
                "COIN" -> R.drawable.img_money_coins
                "HEART" -> R.drawable.img_heart
                "FOOD" -> R.drawable.img_hamburger
                "SHOPPING" -> R.drawable.img_bag
                "COFFEE" -> R.drawable.img_coffee
                "PRESENT" -> R.drawable.img_gift
                "BOOK" -> R.drawable.img_book
                "MONEY" -> R.drawable.img_money
                else -> R.drawable.img_gift
            }

            binding.imageViewHomePromiseCard.setImageResource(imageResource)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePromiseViewHolder {

        val binding = ItemHomePromiseCardBinding.inflate(LayoutInflater.from(parent.context))
        val homePromiseViewHolder = HomePromiseViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return homePromiseViewHolder
    }

    override fun getItemCount(): Int {
        return myPromiseList.size
    }

    override fun onBindViewHolder(holder: HomePromiseViewHolder, position: Int) {
        holder.bind(myPromiseList[position])
    }

    fun getPromiseListSize(): Int {
        return myPromiseList.size
    }

    fun updatePromiseData(newData: List<PromiseDetail>) {
        myPromiseList = newData.toMutableList()
        notifyDataSetChanged()
    }

}