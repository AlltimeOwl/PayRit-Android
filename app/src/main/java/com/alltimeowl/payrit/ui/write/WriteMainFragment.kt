package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentWriteMainBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class WriteMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentWriteMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentWriteMainBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.showBottomNavigationView()

            // 사진 불러오기
            val imageLoader = ImageLoader.Builder(requireContext())
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            val request = ImageRequest.Builder(requireContext())
                .data("https://github.com/wjdwntjd55/Blog/assets/73345198/b1b7c422-0946-4cfe-8f86-ac00fdc1f06c")
                .target(imageViewWritePayRit)
                .build()

            imageLoader.enqueue(request)

            val imageLoaderTwo = ImageLoader.Builder(requireContext())
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            val requestTwo = ImageRequest.Builder(requireContext())
                .data("https://github.com/wjdwntjd55/Blog/assets/73345198/8e916403-3661-46e8-b14c-7c99348bcb0a")
                .target(imageViewWritePromise)
                .build()

            imageLoaderTwo.enqueue(requestTwo)

            // 차용증 작성하기 클릭
            cardViewWriteIouWriteMain.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_MAIN_FRAGMENT, true, null)
            }

        }

    }

}