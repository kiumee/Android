package com.kotlin.kiumee.presentation.menu

import com.kotlin.kiumee.BuildConfig.SOCKET_URL
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity.Companion.VIEW_TYPE_USER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object SocketClient {
    private const val ip = SOCKET_URL
    private const val port = 5000

    private lateinit var socket: Socket
    private lateinit var menuActivity: MenuActivity

    private fun connect(activity: MenuActivity) {
        menuActivity = activity
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 5000)
                Timber.tag("socket").d("Socket connected")

                // 소켓 연결 후 데이터 수신을 위한 스레드 시작
                startSocketReader()
            } catch (e: IOException) {
                Timber.tag("socket").e("Socket connection error: ${e.message}")
                delay(2000)
            }
        }
    }

    private fun startSocketReader() {
        Thread {
            try {
                Timber.tag("socket").d("데이터 받는 중!")
                val inputStream = socket.getInputStream()
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int

                if (inputStream != null) {
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        Timber.tag("socket").d("루프 돌고 있음!")
                        byteArrayOutputStream.write(buffer, 0, bytesRead)
                        byteArrayOutputStream.flush()

                        val decodedString = byteArrayOutputStream.toString("UTF-8")
                        Timber.tag("socket").d("서버로부터 받은 데이터 decodedString : $decodedString")

                        menuActivity.addChatItem(ChatEntity(VIEW_TYPE_USER, decodedString))
                        byteArrayOutputStream.reset()
                    }
                }
            } catch (e: IOException) {
                Timber.tag("socket").e("Error reading from server: ${e.message}")
                if (socket.isClosed) {
                    Timber.tag("socket").e("Socket is closed, attempting to reconnect")
                    connect(menuActivity)
                }
            }
        }.start()
    }

    private fun sendAudio(audioData: ByteArray) {
        // Thread {
        try {
            val outputStream = socket.getOutputStream()
            Timber.tag("socket").d("됨")
            if (!menuActivity.clicked) {
                outputStream.write(audioData.take(17).toByteArray())
                outputStream.flush()
                return
            }
            outputStream.write(audioData)
            outputStream.flush()
        } catch (e: Exception) {
            Timber.tag("socket").e("Error sending audio: ${e.message}")
            connect(menuActivity)
        }
        // }.start()
    }

    fun pipeSendSocket(audioData: ByteArray) {
        sendAudio(audioData)
    }

    fun pipeConnectSocket(activity: MenuActivity) {
        connect(activity)
    }

    fun pipeDisconnectSocket() {
        disconnect()
    }

    private fun disconnect() {
        try {
            Timber.tag("socket").d("Socket disconnect")
            socket.close()
        } catch (e: Exception) {
            Timber.tag("socket").e("Error closing socket: ${e.message}")
        }
    }
}
