package com.vadson40.peripherymanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadson40.peripheral.api.PeripheralManager
import com.vadson40.peripheral.api.model.AudioDeviceType
import com.vadson40.peripheral.api.model.LevelChangeRequest
import com.vadson40.peripheral.api.model.PeripheralRequest
import com.vadson40.peripherymanager.domain.manager.MediaPlayerManager
import com.vadson40.peripherymanager.presentation.model.AudioOutputDeviceType
import com.vadson40.peripherymanager.presentation.model.AudioOutputDeviceVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
class MainViewModel(
    private val peripheralManager: PeripheralManager,
    private val mediaManager: MediaPlayerManager
) : ViewModel() {

    private data class DataState(
        val audioDevice: AudioOutputDeviceVO,
        val audioDeviceList: List<AudioOutputDeviceVO>,
        val volumeLevel: Int
    ) {
        companion object {
            val EMPTY = DataState(
                audioDevice = AudioOutputDeviceVO.EMPTY,
                audioDeviceList = listOf(AudioOutputDeviceVO.EMPTY),
                volumeLevel = 5
            )
        }
    }
    private val dataState = MutableStateFlow(DataState.EMPTY)
    val uiState = dataState
        .map { data ->
            UiState.Success(
                selected = data.audioDevice,
                devicesList = data.audioDeviceList,
                volumeLevel = data.volumeLevel
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loading)

    init {
        peripheralManager.peripheralState
            .onEach { result ->
                dataState.update { current ->
                    current.copy(
                        audioDevice = result.audioOutput.toAudioOutputDeviceVO(),
                        audioDeviceList = result.audioOutputs.map { it.toAudioOutputDeviceVO() }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun playMusic() {
        mediaManager.play()
    }

    fun stopMusic() {
        mediaManager.stop()
    }

    fun setVolumeLevel(value: Int) {
        viewModelScope.launch {
            runCatching {
                peripheralManager.request(PeripheralRequest.VolumeLevelChange(LevelChangeRequest(value)))
            }.onSuccess {
                dataState.update { current ->
                    current.copy(
                        volumeLevel = value
                    )
                }
            }.onFailure {
                // TODO
            }
        }
    }

    private fun AudioDeviceType.toAudioOutputDeviceVO(): AudioOutputDeviceVO {
        val type = when (this) {
            is AudioDeviceType.Earspeaker -> AudioOutputDeviceType.EARPIECE
            is AudioDeviceType.Loudspeaker -> AudioOutputDeviceType.SPEAKER
            is AudioDeviceType.Bluetooth -> AudioOutputDeviceType.BLUETOOTH
            is AudioDeviceType.Headset -> AudioOutputDeviceType.WIRED_HEADSET
            is AudioDeviceType.Unknown -> AudioOutputDeviceType.UNKNOWN
        }
        return AudioOutputDeviceVO(
            deviceId = deviceId,
            type = type
        )
    }
}

sealed class UiState {

    data object Loading: UiState()

    data class Success(
        val selected: AudioOutputDeviceVO,
        val devicesList: List<AudioOutputDeviceVO>,
        val volumeLevel: Int
    ): UiState()

    data class Error(val message: String): UiState()

}