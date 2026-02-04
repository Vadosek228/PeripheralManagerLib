package com.vadson40.peripherymanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
        setContent {
            val context = LocalContext.current
            PeripheryManagerTheme {
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

                        Button(
                            onClick = {
                                audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.Earpiece)
                            },
                            content = { Text("Earpiece") }
                        )

                        Button(
                            onClick = {
//                                // В реальном приложении — выбор устройства из списка
//                                val pairedDevices = btManager.getConnectedDevices()
//                                if (pairedDevices != null && pairedDevices.isNotEmpty()) {
//                                    audioOutputManager.connectBluetoothDevice(pairedDevices.first())
//                                    audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.BluetoothHeadset)
//                                } else {
//                                    Toast.makeText(context, "No paired Bluetooth device", Toast.LENGTH_SHORT).show()
//                                }


                                val pairedDevices = btManager.getConnectedDevices()
                                if (pairedDevices != null && pairedDevices.isNotEmpty()) {
                                    audioOutputManager.connectBluetoothDevice(pairedDevices.first())
                                    audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.BluetoothA2DP)
                                } else {
                                    Toast.makeText(context, "No paired Bluetooth device", Toast.LENGTH_SHORT).show()
                                }
                            },
                            content = { Text("Bloothos") }
                        )


//                        buttonBluetoothCall.setOnClickListener {
//                            val pairedDevices = bluetoothAdapter?.bondedDevices
//                            if (pairedDevices != null && pairedDevices.isNotEmpty()) {
//                                audioManager.connectBluetoothDevice(pairedDevices.first())
//                                audioManager.setOutputMode(AudioOutputManager.OutputMode.BluetoothHFP)
//                            } else {
//                                Toast.makeText(this, "No paired Bluetooth device", Toast.LENGTH_SHORT).show()
//                            }
//                        }

                        Button(
                            onClick = {
                                audioOutputManager.setOutputMode(AudioOutputManager.OutputMode.Speaker)
                            },
                            content = { Text("Speaker") }
                        )
                    }
                }
            }
        }

        mediaPlayerMy = MediaPlayerMy(this)
        audioOutputManager = AudioOutputManager(this, mediaPlayerMy)

//        btManager = BluetoothAudioManager(this)
        btManager = BluetoothAudioManager(this)
        if (btManager.hasPermissions()) {
            btManager.init()
        } else {
            requestBluetoothPermissions()
        }

//        // Пример: кнопка для выбора устройства
//        buttonSelectDevice.setOnClickListener {
//            val devices = btManager.getConnectedDevices()
//            if (devices.isNotEmpty()) {
//                // В реальном приложении — диалоговое окно выбора
//                btManager.setAudioOutput(devices[0])
//            } else {
//                Toast.makeText(this, "No connected devices", Toast.LENGTH_SHORT).show()
//            }
//        }
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