package com.vadson40.peripheral.api.model

import android.media.AudioManager

/**
 * Data for the volume level setting request
 *
 * @param volume - Volume level to set
 * @param streamType - The stream type to install, the default for music
 */
data class LevelChangeRequest(
    val volume: Int,
    val streamType: Int = AudioManager.STREAM_MUSIC
)