package com.alltimeowl.payrit.ui.promise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentPromiseMakeBinding
import com.alltimeowl.payrit.databinding.ItemPromiseMakeBinding

class PromiseMakeAdapter(val fragment: FragmentPromiseMakeBinding, private val listener: OnImageTypeSelectedListener): RecyclerView.Adapter<PromiseMakeAdapter.PromiseMakeViewHolder>() {

    var imageType = ""

    inner class PromiseMakeViewHolder(val binding: ItemPromiseMakeBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {

                val imageResource = when(adapterPosition) {
                    0 -> {
                        imageType = "COIN"
                        R.drawable.img_money_coins
                    }

                    1 -> {
                        imageType = "HEART"
                        R.drawable.img_heart
                    }

                    2 -> {
                        imageType = "FOOD"
                        R.drawable.img_hamburger
                    }

                    3 -> {
                        imageType = "SHOPPING"
                        R.drawable.img_bag
                    }

                    4 -> {
                        imageType = "COFFEE"
                        R.drawable.img_coffee
                    }

                    5 -> {
                        imageType = "PRESENT"
                        R.drawable.img_gift
                    }

                    6 -> {
                        imageType = "BOOK"
                        R.drawable.img_book
                    }

                    7 -> {
                        imageType = "MONEY"
                        R.drawable.img_money
                    }

                    else -> 0
                }
                fragment.imageViewCardPromiseMake.setImageResource(imageResource)
                listener.onImageTypeSelected(imageType)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseMakeViewHolder {

        val binding = ItemPromiseMakeBinding.inflate(LayoutInflater.from(parent.context))
        val promiseMakeViewHolder = PromiseMakeViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return promiseMakeViewHolder
    }

    override fun getItemCount(): Int {
        return 8
    }

    override fun onBindViewHolder(holder: PromiseMakeViewHolder, position: Int) {
        when (position) {
            0 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_money_coins)
            1 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_heart)
            2 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_hamburger)
            3 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_bag)
            4 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_coffee)
            5 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_gift)
            6 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_book)
            7 -> holder.binding.imageViewPromiseMake.setImageResource(R.drawable.img_money)
        }
    }

    interface OnImageTypeSelectedListener {
        fun onImageTypeSelected(imageType: String)
    }

}