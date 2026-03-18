package com.vadson40.peripheral.api.model

/**
 * Request to change peripheral.
 */
sealed interface PeripheralRequest<out R : PeripheralResult> {

    /**
     * Request to turn mic on or off.
     */
    @JvmInline
    value class Microphone(val enable: Boolean) : PeripheralRequest<PeripheralResult.Microphone>

    /**
     * Request to switch audio device.
     */
    @JvmInline
    value class AudioOutput(val deviceId: Int) : PeripheralRequest<PeripheralResult.AudioOutput>

    /**
     * Request for volume level change
     */
    @JvmInline
    value class VolumeLevelChange(val value: LevelChangeRequest) : PeripheralRequest<PeripheralResult.VolumeLevelChange>
}