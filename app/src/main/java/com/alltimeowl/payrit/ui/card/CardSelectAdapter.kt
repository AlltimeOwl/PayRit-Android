package com.alltimeowl.payrit.ui.card

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentCardSelectBinding
import com.alltimeowl.payrit.databinding.ItemEvenCardBinding
import com.alltimeowl.payrit.databinding.ItemOddCardBinding

class CardSelectAdapter(private val fragmentCardSelectBinding: FragmentCardSelectBinding ,private val items: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemStates = MutableList(items.size) { false }

    init {
        Log.d("CardSelectFragment", "items : ${items}")
    }

    inner class CardSelectOddViewHolder(val binding: ItemOddCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val item = items[position]
            val isClicked = itemStates[position]

            // 초기 상태 또는 클릭 상태에 따라 이미지 설정
            if (isClicked) {
                when (item) {
                    "당첨" -> binding.imageViewOddCard.setImageResource(R.drawable.img_winning_card)
                    "통과" -> binding.imageViewOddCard.setImageResource(R.drawable.img_gray_card)
                }
            } else {
                binding.imageViewOddCard.setImageResource(R.drawable.img_mint_card)
                binding.motionLayout.transitionToStart()
            }

            if (!isClicked) { // 클릭되지 않은 상태라면 클릭 리스너를 설정
                binding.imageViewOddCard.setOnClickListener {

                    fragmentCardSelectBinding.textViewTitleCardSelect.text = "축하드려요!\n${position+1}번째 카드의\n결과는'${items[position]}'입니다."

                    // 클릭 상태 변경
                    itemStates[position] = !isClicked

                    // 카드를 올립니다.
                    binding.motionLayout.transitionToEnd()

                    when (item) {
                        "당첨" -> binding.imageViewOddCard.setImageResource(R.drawable.img_winning_symmetry_card)
                        "통과" -> binding.imageViewOddCard.setImageResource(R.drawable.img_pass_card)
                    }

                    // Handler를 사용하여 일정 시간 후에 자동으로 카드를 원래 위치로 돌려놓습니다.
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        binding.motionLayout.transitionToStart()

                        // 카드가 원래 위치로 돌아온 후에, "통과"인 경우의 이미지 변경 로직
                        when (item) {
                            "당첨" -> binding.imageViewOddCard.setImageResource(R.drawable.img_winning_card)
                            "통과" -> binding.imageViewOddCard.setImageResource(R.drawable.img_gray_card)
                        }
                        binding.imageViewOddCard.setOnClickListener(null)
                    }, 2000)
                }
            } else {
                // 이미 클릭 처리가 완료된 경우 클릭 리스너를 설정하지 않음
                binding.imageViewOddCard.setOnClickListener(null)
            }

        }
    }

    inner class CardSelectEvenViewHolder(val binding: ItemEvenCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = items[position]
            val isClicked = itemStates[position]

            if (isClicked) {
                when (item) {
                    "당첨" -> binding.imageViewOddCard.setImageResource(R.drawable.img_winning_card)
                    "통과" -> binding.imageViewOddCard.setImageResource(R.drawable.img_gray_card)
                }
            } else {
                binding.imageViewOddCard.setImageResource(R.drawable.img_white_card)
                binding.motionLayout.transitionToStart()
            }

            if (!isClicked) { 
                binding.imageViewOddCard.setOnClickListener {

                    fragmentCardSelectBinding.textViewTitleCardSelect.text = "축하드려요!\n${position+1}번째 카드의\n결과는'${items[position]}'입니다."

                    itemStates[position] = !isClicked

                    binding.motionLayout.transitionToEnd()

                    when (item) {
                        "당첨" -> binding.imageViewOddCard.setImageResource(R.drawable.img_winning_symmetry_card)
                        "통과" -> binding.imageViewOddCard.setImageResource(R.drawable.img_pass_card)
                    }

                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        binding.motionLayout.transitionToStart()

                        when (item) {
                            "당첨" -> binding.imageViewOddCard.setImageResource(R.drawable.img_winning_card)
                            "통과" -> binding.imageViewOddCard.setImageResource(R.drawable.img_gray_card)
                        }

                        binding.imageViewOddCard.setOnClickListener(null)
                    }, 2000)
                }
            } else {
                binding.imageViewOddCard.setOnClickListener(null)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ODD) {
            val binding = ItemOddCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CardSelectOddViewHolder(binding)
        } else {
            val binding = ItemEvenCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CardSelectEvenViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reversedPosition = itemCount - position - 1 // 아이템 순서를 거꾸로 변경
        if (holder is CardSelectOddViewHolder) {
            // 홀수 위치에서의 데이터 바인딩 로직
            holder.bind(reversedPosition)
        } else if (holder is CardSelectEvenViewHolder) {
            // 짝수 위치에서의 데이터 바인딩 로직
            holder.bind(reversedPosition)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val reversedPosition = itemCount - position - 1
        return if (reversedPosition % 2 == 0) VIEW_TYPE_ODD else VIEW_TYPE_EVEN
    }

    companion object {
        private const val VIEW_TYPE_ODD = 1
        private const val VIEW_TYPE_EVEN = 2
    }

}

