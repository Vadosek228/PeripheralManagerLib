package com.vadson40.peripheral.api.model

/**
 * The status of peripheral.
 *
 * @param microphone microphone enabled or disabled
 * @param audioOutput selected sound output device
 * @param audioOutputs list of available sound output devices
 */
data class PeripheralState(
    val microphone: Boolean,
    val audioOutput: AudioDeviceType,
    val audioOutputs: Set<AudioDeviceType>
)