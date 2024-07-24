package com.kotlin.kiumee.presentation.menu

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.kotlin.kiumee.core.util.context.toast

class VoiceInput : Service() {
    private lateinit var audioRecord: AudioRecord
    private lateinit var buffer: ByteArray
    private var isRecording = false

    // 오디오 캡처 초기화
    private val sampleRate = 16000
    private val bufferSizeInBytes = 640
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = bufferSizeInBytes * 2

    val BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(
        sampleRate,
        channelConfig,
        audioFormat
    ) * 4

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toast("음성 인식 권한이 없습니다.")
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )

        buffer = ByteArray(bufferSizeInBytes)

        startAudioCapture()
    }

    private fun startAudioCapture() {
        // toast("음성인식 시작")

        isRecording = true
        audioRecord.startRecording()

        Thread {
            while (isRecording) {
                val bytesRead = audioRecord.read(buffer, 0, bufferSizeInBytes)

                if (bytesRead > 0) { // 음성 데이터가 있는 경우
                    // 오디오 데이터를 서버로 전송
                    val audioData = buffer.copyOf(bytesRead)
                    // Timber.tag("socket").d(audioData.contentToString())
                    // SocketClient.sendAudio(audioData) // 오디오 데이터 전송
                    SocketClient.pipeSendSocket(audioData)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudioCapture()
        // toast("음성인식 종료")
    }

    fun stopAudioCapture() {
        isRecording = false
        try {
            audioRecord.stop()
            audioRecord.release()
        } catch (exception: Exception) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize
                )
                audioRecord.stop()
                audioRecord.release()
            }
        }
    }
}
