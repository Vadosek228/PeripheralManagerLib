package com.vadson40.peripherymanager.presentation.model

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
data class AudioOutputDeviceVO(
    val deviceId: Int,
    val type: AudioOutputDeviceType
) {
    companion object {
        val EMPTY = AudioOutputDeviceVO(
            deviceId = 1,
            type = AudioOutputDeviceType.EARPIECE
        )
    }
}