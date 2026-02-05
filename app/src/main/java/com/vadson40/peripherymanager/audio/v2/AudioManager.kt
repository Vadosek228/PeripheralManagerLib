package com.vadson40.peripherymanager.audio.v2

import android.content.Context
import com.vadson40.peripherymanager.model.AudioOutputDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @author Akulinin Vladislav
 * @since 05.02.2026
 */
class AudioManager(
    private val context: Context
) {
    private val dataState = MutableStateFlow(AudioManagerUiState.EMPTY)
    val uiState: StateFlow<AudioManagerUiState> = dataState

    fun onClickDevice(device: AudioOutputDevice) {

    }

    fun updateVolumeLevel(volume: Int) {

    }

    init {

    }

}