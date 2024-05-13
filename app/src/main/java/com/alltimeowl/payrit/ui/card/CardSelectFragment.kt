package com.alltimeowl.payrit.ui.card

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.databinding.FragmentCardSelectBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import kotlin.random.Random


class CardSelectFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentCardSelectBinding

    private var headcount = 0
    private lateinit var resultList: List<String>

    val TAG = "CardSelectFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentCardSelectBinding.inflate(layoutInflater)

        headcount = arguments?.getInt("headcount") ?: 0

        initializeResultList()
        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarCardSelect.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CARD_SELECT_FRAGMENT)
                }
            }

            textViewTitleCardSelect.text = "총 ${headcount}명의 랜덤 카드 중에서\n차례대로 선택해주세요!"

            recyclerViewCardSelect.run {

                adapter = CardSelectAdapter(binding, resultList)

                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }

                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val position = parent.getChildAdapterPosition(view)
                        // 첫 번째 아이템을 제외하고 모든 아이템에 적용
                        if (position != (adapter?.itemCount?: 0) - 1)  {
                            val dpToPx = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                60f, // 겹치는 정도를 늘림
                                context.resources.displayMetrics
                            ).toInt() * -1

                            outRect.left = dpToPx
                        }
                    }
                })
            }
        }
    }

    private fun initializeResultList() {
        // 모든 값이 "통과"로 초기화된 리스트 생성
        val tempList = MutableList(headcount) { "통과" }
        // 랜덤 위치 선택
        val winningIndex = Random.nextInt(headcount)
        // 선택된 위치를 "당첨"으로 변경
        tempList[winningIndex] = "당첨"
        resultList = tempList

        Log.d(TAG, "처음 resultList : $resultList")
    }

}