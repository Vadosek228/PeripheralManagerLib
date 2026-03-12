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
import com.vadson40.peripheral.impl.utils.Log.w
import com.vadson40.peripheral.impl.utils.TAG_PERIPHERAL
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Implementation of Peripheral Manager for Android 12 — SDK S (API level 31).
 *
 * @author Vladislav Akulinin
 * @since 25.02.2026
 */
@RequiresApi(Build.VERSION_CODES.S)
internal class PeripheralManagerSImpl(
    context: Context,
    dispatcher: CoroutineDispatcher,
    permissionManager: PermissionManager
) : BasePeripheralManagerImpl(context, dispatcher, permissionManager) {

    override fun refreshAudioOutputDevices() {
        val currentDeviceInfo: AudioDeviceInfo? = audioManager.communicationDevice
        val audioDeviceInfoList: List<AudioDeviceInfo> = audioManager.availableCommunicationDevices

        onAudioDevicesUpdated(currentDeviceInfo, audioDeviceInfoList)
    }

    override fun switchAudioOutput(device: AudioDeviceType): PeripheralResult.AudioOutput {
        Log.tag(TAG_PERIPHERAL).i("Switch device to $device start method.")

        if (audioManager.mode != AudioManager.MODE_IN_COMMUNICATION) {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        }

        if (device is AudioDeviceType.Earspeaker) {
            audioManager.clearCommunicationDevice()
            return PeripheralResult.AudioOutput.Success
        } else {
            val devices = audioManager.availableCommunicationDevices
            val setDevice = devices.find { it.id == device.deviceId }
            if (setDevice != null) {
                val success = audioManager.setCommunicationDevice(setDevice)
                if (!success) {
                    Log.tag(TAG_PERIPHERAL).w("Setting the output deviceId = ${setDevice.id} failed...")
                    return PeripheralResult.AudioOutput.Error
                }
                return PeripheralResult.AudioOutput.Success
            } else {
                Log.tag(TAG_PERIPHERAL).w("Audio device not found in available device... deviceId = ${device.deviceId}")
                return PeripheralResult.AudioOutput.Error
            }
        }
    }
}