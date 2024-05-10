package com.alltimeowl.payrit.ui.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.alltimeowl.payrit.databinding.ItemBannerOneBinding
import com.alltimeowl.payrit.databinding.ItemBannerThreeBinding
import com.alltimeowl.payrit.databinding.ItemBannerTwoBinding
import com.bumptech.glide.Glide


class HomeViewPagerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val BANNER_ONE_VIEW_TYPE = 0
    private val BANNER_TWO_VIEW_TYPE = 1
    private val BANNER_THREE_VIEW_TYPE = 2

    inner class ViewPagerViewHolderOne(private val binding: ItemBannerOneBinding) : RecyclerView.ViewHolder(binding.root) {
        // Banner One에 대한 ViewHolder 내용
        init {
            binding.textViewShowBannerOne.setOnClickListener {
                val context = it.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://payrit.info"))
                context.startActivity(intent)
            }

        }
    }

    inner class ViewPagerViewHolderTwo(private val binding: ItemBannerTwoBinding) : RecyclerView.ViewHolder(binding.root) {
        // Banner Two에 대한 ViewHolder 내용
        init {
            binding.textViewShowBannerTwo.setOnClickListener {
                val context = it.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://payrit.info"))
                context.startActivity(intent)
            }
        }
    }

    inner class ViewPagerViewHolderThree(private val binding: ItemBannerThreeBinding) : RecyclerView.ViewHolder(binding.root) {
        // Banner Three에 대한 ViewHolder 내용
        init {
            binding.textViewShowBannerThree.setOnClickListener {
                val context = it.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://payrit.info"))
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BANNER_ONE_VIEW_TYPE -> {
                val binding = ItemBannerOneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewPagerViewHolderOne(binding)
            }
            BANNER_TWO_VIEW_TYPE -> {
                val binding = ItemBannerTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewPagerViewHolderTwo(binding)
            }
            BANNER_THREE_VIEW_TYPE -> {
                val binding = ItemBannerThreeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewPagerViewHolderThree(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 3) {
            0 -> BANNER_ONE_VIEW_TYPE
            1 -> BANNER_TWO_VIEW_TYPE
            else -> BANNER_THREE_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            BANNER_ONE_VIEW_TYPE -> {
                // Banner One에 대한 처리
            }
            BANNER_TWO_VIEW_TYPE -> {
                // Banner Two에 대한 처리
            }
            BANNER_THREE_VIEW_TYPE -> {
                // Banner Three에 대한 처리
            }
        }
    }

}