package com.vadson40.peripherymanager.audio.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vadson40.peripherymanager.model.AudioOutputDevice
import com.vadson40.peripherymanager.ui.theme.PeripheryManagerTheme

/**
 * @param selected - выбранное
 * @param list - список доступных устройств
 * @param volumeLevel - уровень громкости
 */
data class AudioManagerUiState(
    val selected: AudioOutputDevice,
    val list: List<AudioOutputDevice>,
    val volumeLevel: Int
) {
    companion object {
        val EMPTY = AudioManagerUiState(
            selected = AudioOutputDevice.EARPIECE,
            list = listOf(AudioOutputDevice.EARPIECE, AudioOutputDevice.SPEAKER),
            volumeLevel = 4
        )
    }
}

@Composable
fun AudioManagerScreenV2(
    state: AudioManagerUiState,
    onClickDevice: (AudioOutputDevice) -> Unit,
    updateVolumeLevel: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            text = "Выбран: ${state.selected.title}",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(top = 24.dp))

        state.list.forEach { device ->
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 6.dp),
                onClick = {
                    onClickDevice(device)
                },
                content = {
                    Text(text = device.title)
                }
            )
        }

        Spacer(modifier = Modifier.padding(top = 24.dp))

        // Регулятор громкости
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Громкость: ${state.volumeLevel}",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = state.volumeLevel.toFloat(),
                    onValueChange = { updateVolumeLevel(it.toInt()) },
                    valueRange = 0f..15f,
                    steps = 14
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 24.dp))

        // Кнопки управления
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { updateVolumeLevel(0)  },
                modifier = Modifier.weight(1f)
            ) {
                Text("Без звука")
            }

            Button(
                onClick = { updateVolumeLevel(4)  },
                modifier = Modifier.weight(1f)
            ) {
                Text("Включить звук")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AudioManagerScreenV2Preview() {
    PeripheryManagerTheme {
        AudioManagerScreenV2(
            state = AudioManagerUiState.EMPTY,
            onClickDevice = {},
            updateVolumeLevel = {}
        )
    }
}