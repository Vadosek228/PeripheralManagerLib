package com.vadson40.peripherymanager

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vadson40.peripherymanager.audio.AudioDeviceManager
import com.vadson40.peripherymanager.audio.AudioOutputDevice
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class AudioManagerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioManagerViewModel::class.java)) {
            val audioDeviceManager = AudioDeviceManager(context)
            return AudioManagerViewModel(audioDeviceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class AudioManagerViewModel(
    private val audioDeviceManager: AudioDeviceManager
) : ViewModel() {

    val currentDevice = audioDeviceManager.currentAudioDevice
    val isBluetoothConnected = audioDeviceManager.isBluetoothConnected
    val isWiredHeadsetConnected = audioDeviceManager.isWiredHeadsetConnected

    private val _volume = MutableStateFlow(audioDeviceManager.getCurrentVolume())
    val volume = _volume.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        // Периодическое обновление состояния устройств
        viewModelScope.launch {
            while (true) {
                audioDeviceManager.updateConnectedDevices()
                _volume.value = audioDeviceManager.getCurrentVolume()
                kotlinx.coroutines.delay(1000) // Обновляем каждую секунду
            }
        }
    }

    fun setAudioOutputDevice(device: AudioOutputDevice) {
        viewModelScope.launch {
            val success = audioDeviceManager.setAudioOutputDevice(device)
            if (!success) {
                _errorMessage.value = "Не удалось переключить на ${device.name}"
            }
        }
    }

    fun setVolume(volume: Int) {
        viewModelScope.launch {
            audioDeviceManager.setVolume(volume)
            _volume.value = audioDeviceManager.getCurrentVolume()
        }
    }

    fun setMute(mute: Boolean) {
        viewModelScope.launch {
            audioDeviceManager.setMute(mute)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioDeviceManager.release()
    }
}