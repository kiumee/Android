package com.kotlin.kiumee.presentation.menu

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.core.util.context.formatAmount
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.data.dto.request.RequestPromptDto
import com.kotlin.kiumee.databinding.ActivityMenuBinding
import com.kotlin.kiumee.presentation.menu.cart.CartAdapter
import com.kotlin.kiumee.presentation.menu.cart.CartEntity
import com.kotlin.kiumee.presentation.menu.cart.CartItemDecorator
import com.kotlin.kiumee.presentation.menu.chat.ChatAdapter
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity.Companion.VIEW_TYPE_JUMI
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity.Companion.VIEW_TYPE_USER
import com.kotlin.kiumee.presentation.menu.chat.ChatItemDecorator
import com.kotlin.kiumee.presentation.menu.chat.guidebtn.GuideBtnAdapter
import com.kotlin.kiumee.presentation.menu.chat.guidebtn.GuideBtnEntity
import com.kotlin.kiumee.presentation.menu.chat.guidebtn.GuideBtnItemDecorator
import com.kotlin.kiumee.presentation.menu.menuviewpager.MenuViewPagerAdapter
import com.kotlin.kiumee.presentation.menu.tab.TabAdapter
import com.kotlin.kiumee.presentation.menu.tab.TabItemDecorator
import com.kotlin.kiumee.presentation.orderfinish.OrderFinishActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.Locale

class MenuActivity : BindingActivity<ActivityMenuBinding>(R.layout.activity_menu) {
    private val menuViewModel by viewModels<MenuViewModel>()

    private val cartSmoothScroller: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
    }
    private val chatSmoothScroller: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
    }
    private val tabSmoothScroller: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
    }

    private var tts: TextToSpeech? = null

    private val cartList = mutableListOf<CartEntity>()
    var clicked = false
    private var lastClickedPosition: Int = 0
    private val chatList = mutableListOf(
        ChatEntity(
            viewType = VIEW_TYPE_JUMI,
            content = "주미에게 버튼을 눌러 대화를 걸어보세요."
        )
    )
    var receiveNowCartInfo = false

    override fun initView() {
        initTextToSpeech()
        initChatAdapter()
        initCartLayoutState()

        initSocketConnect()

        initObserveGetMenu()
        initObservePostPrompt()
        // 더미용
        initObservePostCasePrompt()
        initObserveGetPrompts()
        initObservePutBilling()

        initCartEmptyBtnClickListener()
        initOrderBtnClickListener()
        initSpeakBtnClickListener()
        initCloseBtnClickListener()
    }

    // TODO: 오픈할 때 열지 말고 대화 켜기 할 때 소켓 연결 열기
    private fun initSocketConnect() {
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) { SocketClient.cc(this@MenuActivity) }
//        }
        SocketClient.pipeConnectSocket(this@MenuActivity)
    }

    private fun initCartEmptyBtnClickListener() {
        binding.ibMenuCartTrashTotal.setOnClickListener {
            cartList.clear()
            initCartLayoutState()
        }
    }

    private fun initTextToSpeech() {
        tts = TextToSpeech(
            this,
            TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.KOREAN)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Timber.tag("tts").e("해당 언어는 지원되지 않습니다.")
                        return@OnInitListener
                    }
                    Timber.tag("tts").e("TTS 세팅 성공!")
                } else {
                    Timber.tag("tts").e("TTS 초기화 실패")
                }
            }
        )
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // 음성 재생이 시작되면
                Timber.tag("tts").d("재생 시작")
                binding.btnMenuSpeak.isClickable = false
            }

            override fun onDone(utteranceId: String?) {
                // 음성 재생이 완료되면
                Timber.tag("tts").d("재생 완료")
                binding.btnMenuSpeak.isClickable = true
            }

            override fun onError(utteranceId: String?) {
                // 음성 재생 중 오류가 발생하면
            }
        })
    }

    private fun runTextToSpeech(string: String) {
        setupSpeakOff()
        tts?.speak(
            string,
            TextToSpeech.QUEUE_FLUSH,
            null,
            TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
        )
        // tts?.playSilentUtterance(750, TextToSpeech.QUEUE_ADD, null) // deley 시간 설정
    }

    private fun initObserveGetPrompts() {
        menuViewModel.getPrompts.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> initGuideBtnAdapter(it.data)
                is UiState.Failure -> Timber.d("실패 : $it")
                is UiState.Loading -> Timber.d("로딩중")
                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    private fun initObservePostPrompt() {
        menuViewModel.postPrompt.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> {
                    receiveNowCartInfo = true
                    addChatItem(it.data)
                    binding.rvMenuChatGuide.isClickable = true
                }

                is UiState.Failure -> Timber.d("실패 : $it")
                is UiState.Loading -> {
                    Timber.d("로딩중")
                    binding.rvMenuChatGuide.isClickable = false
                }

                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    // 더미용
    private fun initObservePostCasePrompt() {
        menuViewModel.postCasePrompt.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> {
                    addChatItem(it.data)
                    binding.rvMenuChatGuide.isClickable = true
                }

                is UiState.Failure -> Timber.d("실패 : $it")
                is UiState.Loading -> {
                    Timber.d("로딩중")
                    binding.rvMenuChatGuide.isClickable = false
                }

                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    fun addChatItem(chatItem: ChatEntity) {
        runOnUiThread {
            if (chatItem.viewType == VIEW_TYPE_USER) {
                menuViewModel.postPrompt(
                    RequestPromptDto(
                        chatItem.content,
                        if (cartList.isEmpty()) {
                            // cartList가 비어있는 경우 빈 목록 대신에 하나의 기본값을 가지는 목록을 생성
                            emptyList()
                        } else {
                            // cartList가 비어있지 않은 경우 해당 목록을 변환하여 사용
                            cartList.map { it.id }
                        }
                    )
                )

                // 더미용
//                menuViewModel.postCasePrompt(
//                    4,
//                    RequestPromptDto(
//                        chatItem.content,
//                        if (cartList.isEmpty()) {
//                            // cartList가 비어있는 경우 빈 목록 대신에 하나의 기본값을 가지는 목록을 생성
//                            emptyList()
//                        } else {
//                            // cartList가 비어있지 않은 경우 해당 목록을 변환하여 사용
//                            cartList.map { it.id }
//                        }
//                    )
//                )
                // 기존 chatList에 새로운 항목 추가
                chatList.add(chatItem)
                binding.rvMenuChat.removeItemDecorationAt(0)
                binding.rvMenuChat.addItemDecoration(ChatItemDecorator(this))
                binding.rvMenuChat.adapter?.notifyItemInserted(chatList.size - 1)
                initChatScrollPointer()
                // initChatAdapter()
            } else {
                // 기존 chatList에 새로운 항목 추가
                chatList.add(chatItem)
                binding.rvMenuChat.removeItemDecorationAt(0)
                binding.rvMenuChat.addItemDecoration(ChatItemDecorator(this))
                binding.rvMenuChat.adapter?.notifyItemInserted(chatList.size - 1)
                initChatScrollPointer()
                // initChatAdapter()
                runTextToSpeech(chatItem.content)
            }
        }
    }

    private fun initSpeakBtnClickListener() {
        binding.btnMenuSpeak.setOnClickListener {
            if (!clicked) {
                setupSpeakOn()
            } else {
                setupSpeakOff()
            }
        }
    }

    private fun setupSpeakOn() {
        clicked = true

        with(binding) {
            btnMenuSpeak.setBackgroundResource(R.drawable.shape_secondary_fill_circle)
            btnMenuSpeak.text = "대화\n끄기"

            avMenuAudio.visibility = View.VISIBLE
            avMenuAudio.playAnimation()
        }

        val intent = Intent(this, VoiceInput::class.java)
        startService(intent)
    }

    private fun setupSpeakOff() {
        clicked = false

        with(binding) {
            btnMenuSpeak.setBackgroundResource(R.drawable.shape_point_fill_circle)
            btnMenuSpeak.text = "대화\n켜기"

            avMenuAudio.visibility = View.GONE
        }

        val intent = Intent(this, VoiceInput::class.java)
        stopService(intent)
    }

    private fun initObserveGetMenu() {
        menuViewModel.getMenu.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> initTabAdapter(it.data)
                is UiState.Failure -> Timber.d("실패 : $it")
                is UiState.Loading -> Timber.d("로딩중")
                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    private fun initTabAdapter(tabData: List<CategoryEntity>) {
        with(binding) {
            vpMenuMenu.adapter = MenuViewPagerAdapter(supportFragmentManager, lifecycle, tabData)
            rvMenuTabContent.adapter = TabAdapter(click = { _, position ->
                layoutMenuMenu.visibility = View.VISIBLE
                layoutMenuChat.visibility = View.GONE
                lastClickedPosition = position
                vpMenuMenu.adapter =
                    MenuViewPagerAdapter(supportFragmentManager, lifecycle, tabData)
                vpMenuMenu.currentItem = position
            }).apply {
                submitList(tabData)
            }

            vpMenuMenu.isUserInputEnabled = false // 스와이프해서 탭 아이템 넘어가는 것을 허용할 것인지?
            if (binding.rvMenuTabContent.itemDecorationCount == 0) {
                rvMenuTabContent.addItemDecoration(TabItemDecorator(this@MenuActivity))
            }
        }
    }

    private fun initCloseBtnClickListener() {
        binding.tvMenuMenuClose.setOnClickListener {
            binding.layoutMenuMenu.visibility = View.GONE
            binding.layoutMenuChat.visibility = View.VISIBLE
        }
    }

    private fun initOrderBtnClickListener() {
        binding.btnMenuCartOrder.setOnClickListener {
            if (cartList.isNotEmpty()) {
                cartList.map { it.id }.let { menuViewModel.putBilling(it) }
            }
        }
    }

    private fun initObservePutBilling() {
        menuViewModel.putBilling.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> {
                    if (clicked) {
                        setupSpeakOff()
                    }
                    SocketClient.pipeDisconnectSocket()
                    startActivity(Intent(this, OrderFinishActivity::class.java))
                }

                is UiState.Failure -> Unit
                is UiState.Empty -> Unit
                is UiState.Loading -> Unit
            }
        }.launchIn(lifecycleScope)
    }

    private fun initCartLayoutState() {
        Timber.tag("cart").d(cartList.size.toString())
        if (cartList.isNotEmpty()) {
            with(binding) {
                rvMenuCart.visibility = View.VISIBLE
                tvMenuCartEmpty.visibility = View.GONE
            }
            initCartAdapter()
        } else {
            initEmptyLayout()
        }
        initCartTotalPrice()
    }

    private fun initEmptyLayout() {
        with(binding) {
            rvMenuCart.visibility = View.GONE
            tvMenuCartEmpty.visibility = View.VISIBLE
        }
    }

    fun addCartItem(cartItem: CartEntity) {
        // 기존 cartList에 새로운 항목 추가
        cartList.add(cartItem)
        initCartLayoutState()
    }

    private fun initCartAdapter() {
        val cartAdapter = CartAdapter { cartItem ->
            cartList.remove(cartItem)
            initCartLayoutState()
        }
        binding.rvMenuCart.adapter = cartAdapter.apply {
            submitList(cartList)
        }

        if (binding.rvMenuCart.itemDecorationCount == 0) {
            binding.rvMenuCart.addItemDecoration(
                CartItemDecorator(this)
            )
        }

        initCartScrollPointer()
    }

    private fun initCartScrollPointer() {
        binding.rvMenuCart.post {
            binding.rvMenuCart.scrollToPosition((binding.rvMenuCart.adapter?.itemCount ?: 1) - 1)

//            binding.rvMenuCart.layoutManager?.startSmoothScroll(
//                cartSmoothScroller.apply {
//                    targetPosition = cartList.size - 1
//                }
//            )
        }
    }

    private fun initCartTotalPrice() {
        // 카트 목록의 가격 합계 계산
        val totalPrice = cartList.sumOf { it.price }
        binding.tvMenuCartTotalPrice.text = "${formatAmount(totalPrice)}원"
    }

    private fun initGuideBtnAdapter(data: List<GuideBtnEntity>) {
        Timber.tag("aaa").d(data.toString())
        with(binding) {
            rvMenuChatGuide.adapter = GuideBtnAdapter(click = { guideBtnData, position ->
                addChatItem(ChatEntity(VIEW_TYPE_USER, guideBtnData))
            }).apply {
                submitList(data)
            }
            rvMenuChatGuide.layoutManager =
                LinearLayoutManager(
                    rvMenuChatGuide.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            rvMenuChatGuide.addItemDecoration(GuideBtnItemDecorator(this@MenuActivity))
        }
    }

    private fun setCartCompareToOrderInfo(orderInfo: List<CartEntity>) {
        Timber.tag("cart").d("수행됨")
        val orderInfoList = orderInfo.map { it.id }
        val currentCartList = cartList.map { it.id }
        Timber.tag("cart").d(orderInfoList.toString())
        Timber.tag("cart").d(currentCartList.toString())

        if (currentCartList != orderInfoList) {
            Timber.tag("cart").d("여기도 수행됨")
            cartList.clear()
            for (item in orderInfo) {
                cartList.add(item)
            }
            initCartLayoutState()
        }
    }

    private fun setTabScrollToPosition(position: Int) {
        with(binding) {
            val itemCount = rvMenuTabContent.adapter?.itemCount ?: 0
            if (position in 0 until itemCount) {
                rvMenuTabContent?.layoutManager?.startSmoothScroll(
                    tabSmoothScroller.apply {
                        targetPosition = position
                    }
                )
                rvMenuTabContent[position].performClick()
                lastClickedPosition = position
                layoutMenuMenu.visibility = View.VISIBLE
                layoutMenuChat.visibility = View.GONE
            } else {
                // position이 유효한 범위를 벗어나면 에러 처리 또는 다른 동작 수행
                Timber.e("Invalid position: $position, itemCount: $itemCount")
            }
        }
    }

    private fun initChatAdapter() {
        binding.rvMenuChat.adapter = ChatAdapter(
            orderInfoCompareToCart = { orderInfo ->
                if (receiveNowCartInfo) {
                    setCartCompareToOrderInfo(orderInfo)
                    receiveNowCartInfo = false
                }
            },
            orderBtnClickListener = {
                if (cartList.isNotEmpty()) {
                    cartList.map { it.id }.let { menuViewModel.putBilling(it) }
                }
            }
        ).apply {
            submitList(chatList)
        }

        initChatScrollPointer()

        if (binding.rvMenuChat.itemDecorationCount == 0) {
            binding.rvMenuChat.addItemDecoration(
                ChatItemDecorator(this)
            )
        }
    }

    private fun initChatScrollPointer() {
        binding.rvMenuChat.post {
            // binding.rvMenuChat.scrollToPosition((binding.rvMenuChat.adapter?.itemCount ?: 1) - 1)
            // binding.rvMenuChat.scrollToPosition(chatList.size - 1)

            binding.rvMenuChat.layoutManager?.startSmoothScroll(
                chatSmoothScroller.apply {
                    targetPosition = chatList.size
                }
            )
        }
    }

    fun getLastClickedPosition(): Int {
        return lastClickedPosition
    }

    override fun onDestroy() {
        VoiceInput().stopAudioCapture()
        SocketClient.pipeDisconnectSocket()
        super.onDestroy()
    }
}
