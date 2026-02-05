package com.vadson40.peripherymanager.audio.v2

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioManager.AudioPlaybackCallback
import android.media.AudioPlaybackConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.vadson40.peripherymanager.model.AudioOutputDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * @author Akulinin Vladislav
 * @since 05.02.2026
 */
class AudioManager(
    private val context: Context,
    private val coroutineContext: CoroutineContext
) {

    private val scope = CoroutineScope(coroutineContext)
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executor { runnable -> handler.post(runnable) }
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val dataState = MutableStateFlow(AudioManagerUiState.EMPTY)
    val uiState: StateFlow<AudioManagerUiState> = dataState

    private val audioDeviceCallback: AudioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo?>?) {
            super.onAudioDevicesAdded(addedDevices)
            Log.i(TAG, "audioDeviceCallback - onAudioDevicesAdded() - ${addedDevices?.map { it?.type?.toAudioType() }}")
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo?>?) {
            super.onAudioDevicesRemoved(removedDevices)
            Log.i(TAG, "audioDeviceCallback - onAudioDevicesRemoved() - ${removedDevices?.map { it?.type?.toAudioType() }}")
        }
    }

    private var audioPlaybackCallback: AudioPlaybackCallback = object : AudioPlaybackCallback() {
        override fun onPlaybackConfigChanged(configs: List<AudioPlaybackConfiguration?>?) {
            super.onPlaybackConfigChanged(configs)
            Log.i(TAG, "audioPlaybackCallback - onPlaybackConfigChanged() - ${configs}")
        }
    }

    fun onClickDevice(device: AudioOutputDevice) {
        Log.i(TAG, "onClickDevice() - device - ${device.name}")
        when (device) {
            AudioOutputDevice.EARPIECE -> setEarpiece()
            AudioOutputDevice.SPEAKER -> setSpeaker()
            AudioOutputDevice.BLUETOOTH -> setBluetooth()
            AudioOutputDevice.WIRED_HEADSET -> setHeadset()
        }

        setVolume(
            when (device) {
                AudioOutputDevice.EARPIECE -> 7
                AudioOutputDevice.SPEAKER -> 6
                AudioOutputDevice.BLUETOOTH -> 5
                AudioOutputDevice.WIRED_HEADSET -> 4
            }
        )
    }

    fun updateVolumeLevel(volume: Int) {
        setVolume(volume)
    }

    init {
//        androidAudioManager.setParameters("audio_auto_routing=off")
        audioManager.registerAudioDeviceCallback(audioDeviceCallback, handler)
        audioManager.registerAudioPlaybackCallback(audioPlaybackCallback, handler)
        initDevice()
    }

    private fun initDevice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //текущее выбранное устройство
            val currentCommunicationDevice: AudioDeviceInfo? = audioManager.communicationDevice
            // Получаем все доступные устройства вывода
            val devices = audioManager.availableCommunicationDevices

            Log.i(TAG, "initDevice() - " +
                    "\n     current - ${currentCommunicationDevice?.type?.toAudioType()}" +
                    "\n     devices - ${devices.map { "{id = ${it.id}, type = ${it.type} - ${it.type.toAudioType()}, name = ${it.productName}" }}}")

            dataState.update { current ->
                current.copy(
                    selected = currentCommunicationDevice?.type?.toAudioType() ?: devices.first().type.toAudioType(),
                    list = devices.map { it.type.toAudioType() }
                )
            }
        } else {

            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val currentDevice = devices.first()

            Log.i(TAG, "initDevice() - < Build.VERSION_CODES.S" +
                    "\n     current - ${currentDevice?.type?.toAudioType()}" +
                    "\n     devices - ${devices.map { "{id = ${it.id}, type = ${it.type} - ${it.type.toAudioType()}, name = ${it.productName}" }}}")

            dataState.update { current ->
                current.copy(
                    selected = currentDevice?.type?.toAudioType() ?: devices.first().type.toAudioType(),
                    list = devices.map { it.type.toAudioType() }
                )
            }
        }

    }

    private fun setEarpiece() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        // Ищем встроенный динамик
        val speaker = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (speaker != null) {
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                // Явно устанавливаем динамик как устройство вывода
                val success = audioManager.setCommunicationDevice(speaker)
                if (success) {
                    Log.d(TAG, "Выбран ${AudioOutputDevice.EARPIECE.title}")
                } else {
                    Log.w(TAG, "Не удалось установить ${AudioOutputDevice.EARPIECE.title}...")
                }
            } else {
                Log.w(TAG, "${AudioOutputDevice.EARPIECE.title} не найден...")
            }
        } else {
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothA2dpOn = false
            audioManager.isSpeakerphoneOn = false
            Log.d(TAG, "Выбран старым способом ${AudioOutputDevice.EARPIECE.title}")
        }
    }

    private fun setSpeaker() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        // Ищем внешний динамик
        val speaker = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (speaker != null) {
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                // Явно устанавливаем динамик как устройство вывода
                val success = audioManager.setCommunicationDevice(speaker)
                if (success) {
                    Log.d(TAG, "Выбран ${AudioOutputDevice.SPEAKER.title}")
                } else {
                    Log.w(TAG, "Не удалось установить ${AudioOutputDevice.SPEAKER.title}...")
                }
            } else {
                Log.w(TAG, "${AudioOutputDevice.SPEAKER.title} не найден...")
            }
        } else {
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothA2dpOn = false
            audioManager.isSpeakerphoneOn = true
            Log.d(TAG, "Выбран старым способом ${AudioOutputDevice.SPEAKER.title}")
        }
    }

    private fun setBluetooth() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val bluetoothDevice = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

                if (bluetoothDevice != null) {
                    audioManager.isSpeakerphoneOn = false
                    val success = audioManager.setCommunicationDevice(bluetoothDevice)
                    if (success) {
                        Log.d(TAG, "Выбран ${AudioOutputDevice.BLUETOOTH.title}")
                    } else {
                        Log.w(TAG, "Не удалось установить ${AudioOutputDevice.BLUETOOTH.title}...")
                    }
                } else {
                    Log.w(TAG, "Не удалось найти доступное ${AudioOutputDevice.BLUETOOTH.title}...")
                }
            } else {
                audioManager.isSpeakerphoneOn = false
                audioManager.isBluetoothA2dpOn = true
                audioManager.startBluetoothSco()
                Log.d(TAG, "Выбран старым способом ${AudioOutputDevice.BLUETOOTH.title}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Не удалось установить ${AudioOutputDevice.BLUETOOTH.title}... EX - ${e.message}")
        }
    }

    private fun setHeadset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

        } else {
            //TODO
        }
    }

    /**
     * Установка громкости
     */
    private fun setVolume(volume: Int) {
        Log.i(TAG, "setVolume(), volume - $volume")
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val safeVolume = volume.coerceIn(0, maxVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, safeVolume, 0)
        dataState.update { it.copy(volumeLevel = safeVolume) }
    }

    companion object {
        const val TAG = "AudioTest"
    }

}


internal fun Int.toAudioType() =
    when (this) {
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
        AudioDeviceInfo.TYPE_BLE_HEADSET -> AudioOutputDevice.BLUETOOTH

        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> AudioOutputDevice.EARPIECE
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioOutputDevice.SPEAKER

        AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
        AudioDeviceInfo.TYPE_WIRED_HEADSET,
        AudioDeviceInfo.TYPE_USB_HEADSET,
        AudioDeviceInfo.TYPE_USB_DEVICE -> AudioOutputDevice.WIRED_HEADSET

        else -> AudioOutputDevice.SPEAKER
    }