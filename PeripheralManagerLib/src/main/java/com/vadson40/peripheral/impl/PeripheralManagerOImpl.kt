package com.vadson40.peripheral.impl

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.vadson40.peripheral.api.model.AudioDeviceType
import com.vadson40.peripheral.api.model.PeripheralResult
import com.vadson40.peripheral.impl.permission.PermissionManager
import com.vadson40.peripheral.impl.utils.Log
import com.vadson40.peripheral.impl.utils.Log.i
import com.vadson40.peripheral.impl.utils.TAG_PERIPHERAL
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Implementation of PeripheralManager for Android 8.1 — SDK O (version 26).
 * Used to maintain peripherals functionality on devices up to SDK version 31.
 *
 * @author Vladislav Akulinin
 * @since 25.02.2026
 */
@RequiresApi(Build.VERSION_CODES.O)
internal class PeripheralManagerOImpl(
    context: Context,
    dispatcher: CoroutineDispatcher,
    permissionManager: PermissionManager
) : BasePeripheralManagerImpl(context, dispatcher, permissionManager) {

    override fun refreshAudioOutputDevices() {
        val audioDeviceInfoList: List<AudioDeviceInfo> = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
        val currentDeviceInfo: AudioDeviceInfo? = audioDeviceInfoList.getCurrentAudioDevice()

        onAudioDevicesUpdated(currentDeviceInfo, audioDeviceInfoList)
    }

    @Suppress("DEPRECATION")
    override fun switchAudioOutput(device: AudioDeviceType): PeripheralResult.AudioOutput {
        Log.tag(TAG_PERIPHERAL).i("Switching audio output (Legacy API) to: $device")

        if (audioManager.mode != AudioManager.MODE_IN_COMMUNICATION) {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        }

        val isBluetooth = device is AudioDeviceType.Bluetooth
        val isLoudspeaker = device is AudioDeviceType.Loudspeaker

        if (isBluetooth) {
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
        } else {
            if (audioManager.isBluetoothScoOn) {
                audioManager.isBluetoothScoOn = false
                audioManager.stopBluetoothSco()
            }
        }

        /* On many devices, enabling speakerphone automatically disables Bluetooth SCO. */
        audioManager.isSpeakerphoneOn = isLoudspeaker

        return PeripheralResult.AudioOutput.Success
    }

    @Suppress("DEPRECATION")
    private fun List<AudioDeviceInfo>.getCurrentAudioDevice(): AudioDeviceInfo? {
        with(audioManager) {
            return when {
                isBluetoothA2dpOn -> AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                isBluetoothScoOn -> AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                isSpeakerphoneOn -> AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
                isWiredHeadsetOn -> AudioDeviceInfo.TYPE_WIRED_HEADSET
                isSpeakerphoneOn -> AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
                else -> AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
            }.let { type ->
                find { it.type == type }
            }
        }
    }
}