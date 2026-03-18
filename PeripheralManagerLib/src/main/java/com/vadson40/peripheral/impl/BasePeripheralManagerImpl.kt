package com.vadson40.peripheral.impl

import android.Manifest
import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.os.Handler
import com.vadson40.peripheral.api.PeripheralManager
import com.vadson40.peripheral.api.model.AudioDeviceType
import com.vadson40.peripheral.api.model.PeripheralRequest
import com.vadson40.peripheral.api.model.PeripheralResult
import com.vadson40.peripheral.api.model.PeripheralState
import com.vadson40.peripheral.api.model.PermissionRequest
import com.vadson40.peripheral.api.PermissionManager
import com.vadson40.peripheral.impl.utils.Log
import com.vadson40.peripheral.impl.utils.Log.d
import com.vadson40.peripheral.impl.utils.Log.i
import com.vadson40.peripheral.impl.utils.Log.w
import com.vadson40.peripheral.impl.utils.TAG_PERIPHERAL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executor

/**
 * @author Akulinin Vladislav
 * @since 02.03.2026
 */
internal abstract class BasePeripheralManagerImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val permissionManager: PermissionManager
) : PeripheralManager {

    private val handler = Handler(context.mainLooper)
    private val executor: Executor by lazy { dispatcher.asExecutor() }

    protected var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _permissionRequests = MutableSharedFlow<PermissionRequest>()
    override val permissionRequest: Flow<PermissionRequest> = _permissionRequests.asSharedFlow()

    private val dataState: MutableStateFlow<PeripheralState> = MutableStateFlow(initAudioDeviceState())
    override val peripheralState: StateFlow<PeripheralState> = dataState.asStateFlow()

    private val audioPlaybackCallback = object : AudioManager.AudioPlaybackCallback() {
        override fun onPlaybackConfigChanged(configs: List<AudioPlaybackConfiguration?>?) {
            super.onPlaybackConfigChanged(configs)
            Log.tag(TAG_PERIPHERAL).d("AudioPlaybackCallback - $configs")
        }
    }

    private val communicationDeviceListener = AudioManager.OnCommunicationDeviceChangedListener { device ->
        val audioDevice = device?.convertToAudioDeviceType()
        Log.tag(TAG_PERIPHERAL).i("The audio output device has changed - $audioDevice, in OnCommunicationDeviceChangedListener")
        audioDevice?.let { setAudioOutput ->
            dataState.update { it.copy(audioOutput = setAudioOutput) }
        }
    }

    private val modeChangedListener = AudioManager.OnModeChangedListener { mode ->
        Log.tag(TAG_PERIPHERAL).i("Mode has changed - ${mode.modeToStringFormat()}")
    }

    /**
     * To update or remove the list of devices.
     */
    private val audioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo?>?) {
            super.onAudioDevicesAdded(addedDevices)
            Log.tag(TAG_PERIPHERAL).i("A new audio output device has been added - ${addedDevices?.map { it?.convertToAudioDeviceType() }}")
            refreshAudioOutputDevices()
        }
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo?>?) {
            super.onAudioDevicesRemoved(removedDevices)
            Log.tag(TAG_PERIPHERAL).i("An audio output device has been removed - ${removedDevices?.map { it?.convertToAudioDeviceType() }}")
            refreshAudioOutputDevices()
        }
    }

    init {
        audioManager.registerAudioDeviceCallback(audioDeviceCallback, handler)
        audioManager.registerAudioPlaybackCallback(audioPlaybackCallback, handler)

        audioManager.addOnCommunicationDeviceChangedListener(executor, communicationDeviceListener)
        audioManager.addOnModeChangedListener(executor, modeChangedListener)

        refreshAudioOutputDevices()
    }

    @Suppress("UNCHECKED_CAST") // Cast is checked manually. Kotlin's system type drawback.
    override suspend fun <R : PeripheralResult> request(change: PeripheralRequest<R>): R {
        Log.tag(TAG_PERIPHERAL).i("Got peripheral change request: $change")

        return when (change) {
            is PeripheralRequest.Microphone -> {
                if (!permissionManager.isMicrophonePermissionGranted()) {
                    Log.tag(TAG_PERIPHERAL).w("Microphone permission is not granted...")
                    _permissionRequests.emit(PermissionRequest(microphone = true))
                    PeripheralResult.Microphone.PermissionRequired(Manifest.permission.RECORD_AUDIO)
                } else {
                    audioManager.isMicrophoneMute = !change.enable
                    dataState.update { it.copy(microphone = change.enable) }
                    PeripheralResult.Microphone.Success
                }
            }
            is PeripheralRequest.AudioOutput -> {
                val deviceAvailable = dataState.value.audioOutputs.firstOrNull { it.deviceId == change.deviceId }
                if (deviceAvailable != null) {
                    switchAudioOutput(deviceAvailable)
                } else {
                    Log.tag(TAG_PERIPHERAL).w("Unable to find the requested audio output device in the current list of available audio devices by deviceId=${change.deviceId}...")
                    PeripheralResult.AudioOutput.Error
                }
            }
        } as R
    }

    abstract fun refreshAudioOutputDevices()

    abstract fun switchAudioOutput(device: AudioDeviceType): PeripheralResult.AudioOutput

    protected fun onAudioDevicesUpdated(
        device: AudioDeviceInfo?,
        devices: List<AudioDeviceInfo>
    ) {
        val currentDevice = device?.convertToAudioDeviceType()
        val audioDeviceSet: Set<AudioDeviceType> = devices.toAudioDeviceTypeSet()

        if (currentDevice == null) {
            Log.tag(TAG_PERIPHERAL).w("Current Device was NULL...")
            return
        }

        Log.tag(TAG_PERIPHERAL).i("PeripheralState change: audioOutput = $currentDevice, audioOutputs = $audioDeviceSet")
        dataState.update { current ->
            current.copy(
                audioOutput = currentDevice,
                audioOutputs = audioDeviceSet
            )
        }

        /* Update the current device if it has changed in terms of priority. */
        val deviceByPriority = audioDeviceSet.first()
        if (deviceByPriority != currentDevice) {
            switchAudioOutput(deviceByPriority)
        }
    }

    private fun AudioDeviceInfo.convertToAudioDeviceType(): AudioDeviceType {
        return when (type) {
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
            AudioDeviceInfo.TYPE_BLE_HEADSET -> AudioDeviceType.Bluetooth(deviceId = id)

            AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> AudioDeviceType.Earspeaker(deviceId = id)
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioDeviceType.Loudspeaker(deviceId = id)

            AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_USB_HEADSET,
            AudioDeviceInfo.TYPE_USB_DEVICE -> AudioDeviceType.Headset(deviceId = id)

            else -> AudioDeviceType.Unknown(deviceId = id)
        }
    }

    private fun List<AudioDeviceInfo>.toAudioDeviceTypeSet(): Set<AudioDeviceType> =
        filter { it.isValidDevice() }
            .sortedByPriority()
            .map { it.convertToAudioDeviceType() }
            .distinctBy { it::class }
            .sortedBy { it.weightByPriority() }
            .toSet()

    /**
     * Because a user might have multiple Bluetooth devices, they are sorted according to their priority.
     * On recent Android versions, there could be multiple Bluetooth devices such as [AudioDeviceInfo.TYPE_BLUETOOTH_SCO] and [AudioDeviceInfo.TYPE_BLE_HEADSET].
     * In this case, however, Bluetooth performance with [AudioDeviceInfo.TYPE_BLUETOOTH_SCO] is inferior,
     * so the devices are prioritized by type to choose [AudioDeviceInfo.TYPE_BLE_HEADSET].
     */
    private fun List<AudioDeviceInfo>.sortedByPriority() =
        sortedByDescending { it.type }

    /**
     * Set priority for each type of output device.
     * For further sorting by priority and selection of initial output device.
     */
    private fun AudioDeviceType.weightByPriority(): Int =
        when (this) {
            is AudioDeviceType.Unknown -> 0
            is AudioDeviceType.Bluetooth -> 10
            is AudioDeviceType.Headset -> 20
            is AudioDeviceType.Earspeaker -> 30
            is AudioDeviceType.Loudspeaker -> 40
        }

    /**
     * Check whether this is an audio output device.
     */
    private fun AudioDeviceInfo.isValidDevice() = when (type) {
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE,
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
        AudioDeviceInfo.TYPE_WIRED_HEADSET,
        AudioDeviceInfo.TYPE_BLE_HEADSET,
        AudioDeviceInfo.TYPE_USB_HEADSET,
        AudioDeviceInfo.TYPE_USB_DEVICE -> true
        else -> false
    }

    /**
     * Initial state of peripherals.
     */
    private fun initAudioDeviceState(): PeripheralState {
        val initEarSpeakerDeviceId = AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
        val microphone = !audioManager.isMicrophoneMute
        return PeripheralState(
            microphone = microphone,
            audioOutput = AudioDeviceType.Earspeaker(deviceId = initEarSpeakerDeviceId),
            audioOutputs = setOf(AudioDeviceType.Earspeaker(deviceId = initEarSpeakerDeviceId))
        )
    }

    private fun Int.modeToStringFormat() =
        when (this) {
            AudioManager.MODE_NORMAL -> MODE_NORMAL_TO_STRING
            AudioManager.MODE_IN_COMMUNICATION -> MODE_IN_COMMUNICATION_TO_STRING
            AudioManager.MODE_IN_CALL -> MODE_IN_CALL_TO_STRING
            AudioManager.MODE_RINGTONE -> MODE_RINGTONE_TO_STRING
            else -> toString()
        }

    private companion object {
        const val MODE_NORMAL_TO_STRING = "MODE_NORMAL"
        const val MODE_IN_COMMUNICATION_TO_STRING = "MODE_IN_COMMUNICATION"
        const val MODE_IN_CALL_TO_STRING = "MODE_IN_CALL"
        const val MODE_RINGTONE_TO_STRING = "MODE_RINGTONE"
    }
}