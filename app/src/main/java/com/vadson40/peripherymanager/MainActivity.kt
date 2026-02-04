package com.vadson40.peripherymanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vadson40.peripherymanager.audio.AudioDeviceManager
import com.vadson40.peripherymanager.audio.AudioOutputDevice
import com.vadson40.peripherymanager.ui.theme.PeripheryManagerTheme

class MainActivity : ComponentActivity() {

    private lateinit var btManager: BluetoothAudioManager
    private lateinit var audioOutputManager: AudioOutputManager
    private lateinit var mediaPlayerMy: MediaPlayerMy


    private val REQUEST_BT_PERMISSIONS = 1001

    private fun requestBluetoothPermissions() {
        val permissions = mutableListOf<String>()

        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        requestPermissions(permissions.toTypedArray(), REQUEST_BT_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BT_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Разрешения получены — инициализируем менеджер
                btManager.init()
            } else {
                Toast.makeText(this, "Bluetooth permissions required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        mediaPlayerMy = MediaPlayerMy(this)

        val viewModel: AudioManagerViewModel = AudioManagerViewModel(
            audioDeviceManager = AudioDeviceManager(this)
        )

        setContent {
//            val context = LocalContext.current
//            PeripheryManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        Button(
                            onClick = {
                                mediaPlayerMy.playTestSound()
                            },
                            content = { Text("media start") }
                        )
                        Button(
                            onClick = {
                                mediaPlayerMy.stopSound()
                            },
                            content = { Text("media stop") }
                        )
//
//                        Button(
//                            onClick = {
//                                audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.Earpiece)
//                            },
//                            content = { Text("Earpiece") }
//                        )
//
//                        Button(
//                            onClick = {
////                                // В реальном приложении — выбор устройства из списка
////                                val pairedDevices = btManager.getConnectedDevices()
////                                if (pairedDevices != null && pairedDevices.isNotEmpty()) {
////                                    audioOutputManager.connectBluetoothDevice(pairedDevices.first())
////                                    audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.BluetoothHeadset)
////                                } else {
////                                    Toast.makeText(context, "No paired Bluetooth device", Toast.LENGTH_SHORT).show()
////                                }
//
//
//                                val pairedDevices = btManager.getConnectedDevices()
//                                if (pairedDevices != null && pairedDevices.isNotEmpty()) {
//                                    audioOutputManager.connectBluetoothDevice(pairedDevices.first())
//                                    audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.BluetoothA2DP)
//                                } else {
//                                    Toast.makeText(context, "No paired Bluetooth device", Toast.LENGTH_SHORT).show()
//                                }
//                            },
//                            content = { Text("Bloothos") }
//                        )
//
//
////                        buttonBluetoothCall.setOnClickListener {
////                            val pairedDevices = bluetoothAdapter?.bondedDevices
////                            if (pairedDevices != null && pairedDevices.isNotEmpty()) {
////                                audioManager.connectBluetoothDevice(pairedDevices.first())
////                                audioManager.setOutputMode(AudioOutputManager.OutputMode.BluetoothHFP)
////                            } else {
////                                Toast.makeText(this, "No paired Bluetooth device", Toast.LENGTH_SHORT).show()
////                            }
////                        }
//
//                        Button(
//                            onClick = {
//                                audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.Speaker)
//                            },
//                            content = { Text("Speaker") }
//                        )
//                    }
//                }
//            }
//        }
//
//        mediaPlayerMy = MediaPlayerMy(this)
//        audioOutputManager = AudioOutputManager(this, mediaPlayerMy)
//
////        btManager = BluetoothAudioManager(this)
//        btManager = BluetoothAudioManager(this)
//        if (btManager.hasPermissions()) {
//            btManager.init()
//        } else {
//            requestBluetoothPermissions()
//        }
//
////        // Пример: кнопка для выбора устройства
////        buttonSelectDevice.setOnClickListener {
////            val devices = btManager.getConnectedDevices()
////            if (devices.isNotEmpty()) {
////                // В реальном приложении — диалоговое окно выбора
////                btManager.setAudioOutput(devices[0])
////            } else {
////                Toast.makeText(this, "No connected devices", Toast.LENGTH_SHORT).show()
////            }
//        }


                        AudioDeviceSelector(viewModel)
                    }
                }
        }
    }

    override fun onDestroy() {
        btManager.release()
        audioOutputManager.release()

        super.onDestroy()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PeripheryManagerTheme {

    }
}

@Composable
fun AudioDeviceSelector(viewModel: AudioManagerViewModel) {
//    val viewModel: AudioManagerViewModel = viewModel(
//        factory = AudioManagerViewModelFactory(
//            LocalContext.current
//        )
//    )

    val currentDevice by viewModel.currentDevice.collectAsState()
    val isBluetoothConnected by viewModel.isBluetoothConnected.collectAsState()
    val isWiredHeadsetConnected by viewModel.isWiredHeadsetConnected.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Отображение текущего устройства
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Текущее устройство:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (currentDevice) {
                        AudioOutputDevice.EARPIECE -> "Ушной динамик"
                        AudioOutputDevice.SPEAKER -> "Громкоговоритель"
                        AudioOutputDevice.WIRED_HEADSET -> "Проводные наушники"
                        AudioOutputDevice.BLUETOOTH -> "Bluetooth"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        // Кнопки выбора устройств
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeviceButton(
                device = AudioOutputDevice.EARPIECE,
                enabled = true,
                onClick = { viewModel.setAudioOutputDevice(AudioOutputDevice.EARPIECE) }
            )

            DeviceButton(
                device = AudioOutputDevice.SPEAKER,
                enabled = true,
                onClick = { viewModel.setAudioOutputDevice(AudioOutputDevice.SPEAKER) }
            )

            DeviceButton(
                device = AudioOutputDevice.WIRED_HEADSET,
                enabled = isWiredHeadsetConnected,
                onClick = { viewModel.setAudioOutputDevice(AudioOutputDevice.WIRED_HEADSET) }
            )

            DeviceButton(
                device = AudioOutputDevice.BLUETOOTH,
                enabled = isBluetoothConnected,
                onClick = { viewModel.setAudioOutputDevice(AudioOutputDevice.BLUETOOTH) }
            )
        }

        // Регулятор громкости
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Громкость: $volume",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = volume.toFloat(),
                    onValueChange = { viewModel.setVolume(it.toInt()) },
                    valueRange = 0f..15f,
                    steps = 14
                )
            }
        }

        // Кнопки управления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.setMute(true) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Без звука")
            }

            Button(
                onClick = { viewModel.setMute(false) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Включить звук")
            }
        }

        // Отображение ошибок
        errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Ошибка") },
                text = { Text(message) },
                confirmButton = {
                    Button(
                        onClick = { viewModel.clearError() }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun DeviceButton(
    device: AudioOutputDevice,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val deviceName = when (device) {
        AudioOutputDevice.EARPIECE -> "Ушной динамик"
        AudioOutputDevice.SPEAKER -> "Громкоговоритель"
        AudioOutputDevice.WIRED_HEADSET -> "Проводные наушники"
        AudioOutputDevice.BLUETOOTH -> "Bluetooth"
    }

    val icon = when (device) {
        AudioOutputDevice.EARPIECE -> Icons.Default.Build
        AudioOutputDevice.SPEAKER -> Icons.Default.Favorite
        AudioOutputDevice.WIRED_HEADSET -> Icons.Default.Face
        AudioOutputDevice.BLUETOOTH -> Icons.Default.Edit
    }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (!enabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(deviceName)
    }
}