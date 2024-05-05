package com.alltimeowl.payrit.ui.promise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentPromiseMainBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class PromiseMainFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseMainBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarPromiseMain.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_MAIN_FRAGMENT)
                }
            }

            // 사진 불러오기
            val imageLoader = ImageLoader.Builder(requireContext())
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            val request = ImageRequest.Builder(requireContext())
                .data("https://github.com/wjdwntjd55/Blog/assets/73345198/8e916403-3661-46e8-b14c-7c99348bcb0a")
                .target(imageViewMakePromiseCardPromiseMain)
                .build()

            imageLoader.enqueue(request)

            val imageLoaderTwo = ImageLoader.Builder(requireContext())
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            val requestTwo = ImageRequest.Builder(requireContext())
                .data("https://github.com/AlltimeOwl/PayRit-Android/assets/73345198/d780b0f1-7c00-4f90-a9d2-83b52d0aa61d")
                .target(imageViewSelectCardPromiseMain)
                .build()

            imageLoaderTwo.enqueue(requestTwo)

        }
    }

}