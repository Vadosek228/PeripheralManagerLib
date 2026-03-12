package com.vadson40.peripheral.api.model

/**
 * Available types of audio devices to choose from.
 *
 * @author Akulinin Vladislav
 * @since 02.03.2026
 */
sealed class AudioDeviceType(
    open val deviceId: Int
) {

    data class Earspeaker(override val deviceId: Int) : AudioDeviceType(deviceId)

    data class Loudspeaker(override val deviceId: Int) : AudioDeviceType(deviceId)

    data class Bluetooth(override val deviceId: Int) : AudioDeviceType(deviceId)

    data class Headset(override val deviceId: Int) : AudioDeviceType(deviceId)

    data class Unknown(override val deviceId: Int) : AudioDeviceType(deviceId)

}