package com.vadson40.peripherymanager

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

@Deprecated(message="Use PeripheralManager instead")
class BluetoothAudioManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var a2dp: BluetoothProfile? = null
    private val connectedDevices = mutableListOf<BluetoothDevice>()

    private var currentOutputDevice: BluetoothDevice? = null

    // Receiver для событий подключения/отключения A2DP
    private val a2dpReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

            when (action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {

                    try {
                        Log.d("BT_AUDIO", "Device connected: ${device?.name}")
                    } catch (ex: SecurityException) {
                        Log.e("BT_AUDIO", "Permission denied: ${ex.message}")
                    }

                    refreshConnectedDevices()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {

                    try {
                        Log.d("BT_AUDIO", "Device disconnected: ${device?.name}")
                    } catch (ex: SecurityException) {
                        Log.e("BT_AUDIO", "Permission denied: ${ex.message}")
                    }


                    refreshConnectedDevices()
                    if (device == currentOutputDevice) {
                        currentOutputDevice = null
                    }
                }
            }
        }
    }

    // Инициализация
    fun init() {
        if (bluetoothAdapter == null) {
            Log.e("BT_AUDIO", "Bluetooth not supported")
            return
        }

        if (!hasPermissions()) {
            Log.w("BT_AUDIO", "Missing Bluetooth permissions")
            // Здесь нужно запросить разрешения у пользователя
            // (см. раздел ниже)
            return
        }

        // Регистрируем receiver и подключаемся к A2DP
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        ContextCompat.registerReceiver(context, a2dpReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        bluetoothAdapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.A2DP) {
                    a2dp = proxy
                    refreshConnectedDevices()
                }
            }

            override fun onServiceDisconnected(profile: Int) {
                if (profile == BluetoothProfile.A2DP) {
                    a2dp = null
                }
            }
        }, BluetoothProfile.A2DP)
    }

    // Обновляем список подключённых A2DP‑устройств
    private fun refreshConnectedDevices() {
        connectedDevices.clear()
        a2dp?.connectedDevices?.forEach { device ->
            if (a2dp?.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED) {
                connectedDevices.add(device)
            }
        }
        try {
            Log.d("BT_AUDIO", "Connected A2DP devices: ${connectedDevices.map { it.name }}")
        } catch (ex: SecurityException) {
            Log.e("BT_AUDIO", "Permission denied: ${ex.message}")
        }
    }

    // Устанавливаем устройство как выход звука
    fun setAudioOutput(device: BluetoothDevice): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Для API 33+ используем setBluetoothA2dpDeviceConnectionPolicy (если нужно автоподключение)
        // Здесь просто переключаем вывод на устройство
        audioManager.startBluetoothSco() // Активируем Bluetooth‑аудио
        audioManager.isBluetoothScoOn = true

        currentOutputDevice = device

        try {
            Log.d("BT_AUDIO", "Audio output set to: ${device.name}")
        } catch (ex: SecurityException) {
            Log.e("BT_AUDIO", "Permission denied: ${ex.message}")
        }
        return true
    }

    // Получаем список подключённых устройств
    fun getConnectedDevices(): List<BluetoothDevice> {
        return connectedDevices
    }

    // Освобождаем ресурсы
    fun release() {
        context.unregisterReceiver(a2dpReceiver)
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.A2DP, a2dp)
    }

    fun hasPermissions(): Boolean {
        val permissionConnect = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val permissionScan = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        // Для API < 31 BLUETOOTH_SCAN не требуется
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionConnect && permissionScan
        } else {
            permissionConnect
        }
    }
}