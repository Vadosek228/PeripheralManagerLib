package com.vadson40.peripherymanager.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vadson40.peripherymanager.presentation.model.AudioOutputDeviceVO
import com.vadson40.peripherymanager.presentation.ui.components.AudioPlayerContent
import com.vadson40.peripherymanager.presentation.ui.components.DeviceButton
import com.vadson40.peripherymanager.presentation.ui.components.ProgressIndicatorFullScreen
import com.vadson40.peripherymanager.presentation.ui.theme.PeripheryManagerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            PeripheryManagerTheme {
                MainContent(
                    uiState = uiState,
                    playClick = { viewModel.playMusic() },
                    stopClick = { viewModel.stopMusic() },
                    deviceClick = {

                    },
                    updateVolumeLevel = { viewModel.setVolumeLevel(it) }
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    uiState: UiState,
    playClick: () -> Unit,
    stopClick: () -> Unit,
    deviceClick: () -> Unit,
    updateVolumeLevel: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AudioPlayerContent(
                playClick = playClick,
                stopClick = stopClick
            )

            when (uiState) {
                is UiState.Loading -> {
                    ProgressIndicatorFullScreen()
                }
                is UiState.Success -> {
                    MainScreenSuccessContent(
                        state = uiState,
                        deviceClick = {
                            //todo
                        },
                        updateVolumeLevel = updateVolumeLevel
                    )
                }
                is UiState.Error -> {
                    //TODO show error
                }
            }
        }
    }
}

@Composable
private fun MainScreenSuccessContent(
    state: UiState.Success,
    deviceClick: (AudioOutputDeviceVO) -> Unit,
    updateVolumeLevel: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            text = "Выбран: ${state.selected.type.title}",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(top = 24.dp))

        state.devicesList.forEach { device ->
            DeviceButton(
                device = device.type,
                onClick = { deviceClick.invoke(device) }
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
                    onValueChange = {
                        updateVolumeLevel(it.toInt())
                    },
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
                onClick = { updateVolumeLevel(0) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Без звука")
            }

            Button(
                onClick = { updateVolumeLevel(4) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Включить звук")
            }
        }

    }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun PreviewMainContent() {
    PeripheryManagerTheme {
        MainContent(
            uiState = UiState.Success(
                selected = AudioOutputDeviceVO.EMPTY,
                devicesList = listOf(AudioOutputDeviceVO.EMPTY, AudioOutputDeviceVO.EMPTY),
                volumeLevel = 1
            ),
            playClick = {},
            stopClick = {},
            deviceClick = {},
            updateVolumeLevel = {}
        )
    }
}