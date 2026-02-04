package com.vadson40.peripherymanager

import android.content.Context
import android.media.AudioManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.util.Log

//class AudioOutputManager(private val context: Context) {
//
//    private val audioManager: AudioManager =
//        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//    private var a2dp: BluetoothProfile? = null
//    private var currentBluetoothDevice: BluetoothDevice? = null
//
//
//    // Перечисление режимов вывода
//    sealed class OutputMode {
//        object InternalSpeakerQuiet : OutputMode()   // Тихий динамик
//        object BluetoothHeadset : OutputMode()        // Bluetooth‑гарнитура
//        object InternalSpeakerLoud : OutputMode()     // Громкий динамик
//    }
//
//    private var currentMode: OutputMode = OutputMode.InternalSpeakerQuiet
//
//    private val a2dpListener = object : BluetoothProfile.ServiceListener {
//        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
//            if (profile == BluetoothProfile.A2DP) {
//                a2dp = proxy
//                Log.d("AUDIO", "A2DP connected")
//            }
//        }
//        override fun onServiceDisconnected(profile: Int) {
//            if (profile == BluetoothProfile.A2DP) {
//                a2dp = null
//                Log.d("AUDIO", "A2DP disconnected")
//            }
//        }
//    }
//
//    init {
//        if (bluetoothAdapter != null) {
//            bluetoothAdapter.getProfileProxy(context, a2dpListener, BluetoothProfile.A2DP)
//        }
//    }
//
//    // Переключение режима вывода
//    fun setOutputMode(mode: OutputMode) {
//        currentMode = mode
//
//        when (mode) {
//            is OutputMode.InternalSpeakerQuiet -> {
//                setInternalSpeaker(volumeLevel = 0.2f)  // 20% громкости
//            }
//            is OutputMode.BluetoothHeadset -> {
//                if (currentBluetoothDevice != null && a2dp != null) {
//                    audioManager.isBluetoothScoOn = false  // Отключаем SCO (голосовой канал)
//                    audioManager.startBluetoothSco()     // Активируем A2DP
//                    Log.d("AUDIO", "Output: Bluetooth headset")
//                } else {
//                    Log.w("AUDIO", "No Bluetooth device connected")
//                    setOutputMode(OutputMode.InternalSpeakerLoud)  // fallback
//                }
//            }
//            is OutputMode.InternalSpeakerLoud -> {
//                setInternalSpeaker(volumeLevel = 1.0f)  // 100% громкости
//            }
//        }
//    }
//
//    // Установка громкости внутреннего динамика
//    private fun setInternalSpeaker(volumeLevel: Float) {
//        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
//        val volume = (maxVolume * volumeLevel).toInt()
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
//        audioManager.isSpeakerphoneOn = true  // Включаем громкий динамик
//        Log.d("AUDIO", "Output: Internal speaker (volume: $volumeLevel)")
//    }
//
//    // Подключение Bluetooth‑устройства (вызывается извне)
//    fun connectBluetoothDevice(device: BluetoothDevice) {
//        currentBluetoothDevice = device
//        // Автоматически переключиться на Bluetooth, если режим установлен
//        if (currentMode is OutputMode.BluetoothHeadset) {
//            setOutputMode(currentMode)
//        }
//    }
//
//    // Освобождение ресурсов
//    fun release() {
//        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.A2DP, a2dp)
//    }
//}

class AudioOutputManager(
    private val context: Context,
    private val mediaPlayer: MediaPlayerMy
) {

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // Для A2DP (музыка)
    private var a2dp: BluetoothProfile? = null
    // Для HFP (звонки)
    private var headset: BluetoothProfile? = null


    private var currentBluetoothDevice: BluetoothDevice? = null


    // Режимы вывода звука
    sealed class OutputMode {
        object Earpiece : OutputMode()        // Ушной динамик (для звонков)
        object Speaker : OutputMode()         // Громкий динамик (мультимедиа)
        object BluetoothA2DP : OutputMode()  // Bluetooth‑гарнитура (музыка)
        object BluetoothHFP : OutputMode()   // Bluetooth‑гарнитура (звонки)
    }

    private var currentMode: OutputMode = OutputMode.Earpiece


    // Слушатели для Bluetooth‑профилей
    private val a2dpListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.A2DP) {
                a2dp = proxy
                Log.d("AUDIO", "A2DP connected")
            }
        }
        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.A2DP) {
                a2dp = null
                Log.d("AUDIO", "A2DP disconnected")
            }
        }
    }

    private val headsetListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                headset = proxy
                Log.d("AUDIO", "HEADSET (HFP) connected")
            }
        }
        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                headset = null
                Log.d("AUDIO", "HEADSET (HFP) disconnected")
            }
        }
    }

    init {
        // Подключаемся к профилям Bluetooth
        if (bluetoothAdapter != null) {
            bluetoothAdapter.getProfileProxy(context, a2dpListener, BluetoothProfile.A2DP)
            bluetoothAdapter.getProfileProxy(context, headsetListener, BluetoothProfile.HEADSET)
        }
    }

    // Переключение режима вывода
    fun setOutputMode(mode: OutputMode) {
        currentMode = mode

        when (mode) {
            is OutputMode.Earpiece -> {
//                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
//                audioManager.isSpeakerphoneOn = false
//                audioManager.isBluetoothScoOn = false
//                Log.d("AUDIO", "Output: Earpiece (phone)")



                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION


//                mediaPlayer.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
                mediaPlayer.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                audioManager.isSpeakerphoneOn = false
                audioManager.isBluetoothScoOn = false
                Log.d("AUDIO", "Output: Earpiece (phone)")
            }
            is OutputMode.Speaker -> {
                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isSpeakerphoneOn = true
                audioManager.setBluetoothScoOn(false)
                Log.d("AUDIO", "Output: Speaker (loud)")
            }
            is OutputMode.BluetoothA2DP -> {
                if (currentBluetoothDevice != null && a2dp != null) {
                    audioManager.mode = AudioManager.MODE_NORMAL
                    audioManager.isSpeakerphoneOn = false
                    audioManager.startBluetoothSco()  // Активируем Bluetooth
                    Log.d("AUDIO", "Output: Bluetooth A2DP (music)")
                } else {
                    Log.w("AUDIO", "No Bluetooth A2DP device connected")
                    setOutputMode(OutputMode.Speaker)  // fallback
                }
            }
            is OutputMode.BluetoothHFP -> {
                if (currentBluetoothDevice != null && headset != null) {
                    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                    audioManager.isSpeakerphoneOn = false
                    audioManager.isBluetoothScoOn = true  // Включаем SCO для звонков
                    Log.d("AUDIO", "Output: Bluetooth HFP (calls)")
                } else {
                    Log.w("AUDIO", "No Bluetooth HFP device connected")
                    setOutputMode(OutputMode.Earpiece)  // fallback
                }
            }
        }
    }

    // Подключение Bluetooth‑устройства
    fun connectBluetoothDevice(device: BluetoothDevice) {
        currentBluetoothDevice = device
        // Если текущий режим — Bluetooth, переключаемся
        if (currentMode is OutputMode.BluetoothA2DP || currentMode is OutputMode.BluetoothHFP) {
            setOutputMode(currentMode)
        }
    }

    // Освобождение ресурсов
    fun release() {
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.A2DP, a2dp)
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, headset)
    }
}