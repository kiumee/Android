package com.kotlin.kiumee.presentation.menu.chat

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.databinding.ItemChatJumiBinding
import com.kotlin.kiumee.databinding.ItemChatUserBinding
import com.kotlin.kiumee.presentation.menu.MenuActivity
import com.kotlin.kiumee.presentation.menu.cart.CartEntity
import com.kotlin.kiumee.presentation.menu.chat.menubtn.MenuBtnAdapter
import timber.log.Timber

class ChatJumiViewHolder(
    private val binding: ItemChatJumiBinding,
    private val orderInfoCompareToCart: (List<CartEntity>) -> Unit,
    private val orderBtnClickListener: () -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: ChatEntity) {
        with(binding) {
            chat = data

            // 추천 메뉴
            if (data.suggestItems != null) {
                rvItemChatJumiBtn.visibility = View.VISIBLE

                val layoutParams = layoutItemChatJumiBackground.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutItemChatJumiBackground.layoutParams = layoutParams

                rvItemChatJumiBtn.apply {
                    adapter = MenuBtnAdapter(click = { cartData, position ->
                        val activity = itemView.context as? MenuActivity
                        activity?.addCartItem(cartData)
                        // context.toast("장바구니에 선택한 메뉴가 담겼습니다.")
                    }).apply { submitList(data.suggestItems) }
                    layoutManager = LinearLayoutManager(
                        binding.rvItemChatJumiBtn.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            } else {
                rvItemChatJumiBtn.visibility = View.GONE
            }

            // 장바구니 정보
            if (data.orderInfo != null) {
                val orderIntoList = data.orderInfo.map { CartEntity(it.id, it.name, it.price) }
                Timber.tag("cart").d(orderIntoList.toString())
                orderInfoCompareToCart.invoke(orderIntoList)
            }

            // 카테고리 포인터 -> 나중에 구현하던가 빼야 함
            if (data.pointerId != null) {
                Timber.tag("pointer").d(data.pointerId.toString())
            }

            // 주문하기
            if (data.doBilling == true) {
                // 5초 후에 작업 수행
                Handler(Looper.getMainLooper()).postDelayed({
                    orderBtnClickListener.invoke()
                }, 6000)
            }
        }
    }
}

class ChatUserViewHolder(private val binding: ItemChatUserBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: ChatEntity) {
        binding.chat = data
    }
}
