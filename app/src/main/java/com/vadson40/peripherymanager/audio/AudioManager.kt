package com.vadson40.peripherymanager.audio

import android.content.Context
import android.media.AudioManager as AndroidAudioManager
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager.AudioPlaybackCallback
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executor
import java.util.logging.Handler

/**
 * Управление устройствами вывода звука на Android
 */
@RequiresApi(Build.VERSION_CODES.S)
class AudioDeviceManager(private val context: Context) {

    private val handler = android.os.Handler(Looper.getMainLooper())
    private val executor = Executor { runnable -> handler.post(runnable) }


    private val androidAudioManager: AndroidAudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AndroidAudioManager

    private val _currentAudioDevice = MutableStateFlow(AudioOutputDevice.SPEAKER)
    val currentAudioDevice: StateFlow<AudioOutputDevice> = _currentAudioDevice.asStateFlow()

    private val _isBluetoothConnected = MutableStateFlow(false)
    val isBluetoothConnected: StateFlow<Boolean> = _isBluetoothConnected.asStateFlow()

    private val _isWiredHeadsetConnected = MutableStateFlow(false)
    val isWiredHeadsetConnected: StateFlow<Boolean> = _isWiredHeadsetConnected.asStateFlow()

    private var audioFocusRequest: AudioFocusRequest? = null
    private var audioPlaybackCallback: AudioPlaybackCallback? = null

    private val onModeChangedListener = AndroidAudioManager.OnModeChangedListener {
        Log.i("AudioTest", "onModeChangedListener - ${it}")
        println("onModeChangedListener - $it")

        androidAudioManager.mode = AndroidAudioManager.MODE_NORMAL
        //todo костыль
        setAudioOutputDevice(_currentAudioDevice.value)
    }

    val audioDeviceCallback: AudioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo?>?) {
            super.onAudioDevicesAdded(addedDevices)
            Log.i("AudioTest", "audioDeviceCallback - onAudioDevicesAdded - ${addedDevices?.map { it?.productName }}")
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo?>?) {
            super.onAudioDevicesRemoved(removedDevices)
            Log.i("AudioTest", "audioDeviceCallback - onAudioDevicesRemoved - ${removedDevices?.map { it?.productName }}")
        }
    }

    val listener =
        AndroidAudioManager.OnCommunicationDeviceChangedListener { device -> // Handle changes
            Log.i("AudioTest", "OnCommunicationDeviceChangedListener - ${device?.productName}")
        }


    init {
        Log.i("AudioTest", "init()")
        androidAudioManager.registerAudioDeviceCallback(audioDeviceCallback, handler)
        androidAudioManager.addOnCommunicationDeviceChangedListener(executor, listener)
        androidAudioManager.addOnModeChangedListener(executor, onModeChangedListener)

        setupAudioDeviceMonitoring()
        updateCurrentDevice()

        androidAudioManager.setParameters("audio_auto_routing=off")
    }

    /**
     * Устанавливает устройство вывода звука
     */
    fun setAudioOutputDevice(device: AudioOutputDevice): Boolean {

//        _currentAudioDevice.value = device
        return when (device) {
            AudioOutputDevice.EARPIECE -> setEarpiece()
            AudioOutputDevice.SPEAKER -> setSpeaker()
            AudioOutputDevice.WIRED_HEADSET -> setWiredHeadset()
            AudioOutputDevice.BLUETOOTH -> setBluetooth()
        }
    }

    /**
     * Переключает на ушной динамик
     */
    private fun setEarpiece(): Boolean {
        return try {
//            disableBluetoothCompletely()
//
////            // Перед переключением отключаем Bluetooth SCO
////            androidAudioManager.stopBluetoothSco()
////            androidAudioManager.isBluetoothScoOn = false
//
//            androidAudioManager.setParameters("audio_force_use=earpiece")
//
//            // Устанавливаем режим
//            androidAudioManager.setMode(AndroidAudioManager.MODE_NORMAL)
//
//            // Отключаем громкоговоритель
//            androidAudioManager.isSpeakerphoneOn = false
//
//            // Для ушного динамика нужно установить режим коммуникации
//            androidAudioManager.mode = AndroidAudioManager.MODE_IN_COMMUNICATION
//
//            // Устанавливаем громкость для ушного динамика
//            setVolumeForDevice(AudioOutputDevice.EARPIECE)
//
//            _currentAudioDevice.value = AudioOutputDevice.EARPIECE

            switchToPhoneEarspicerApi33()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Переключает на громкоговоритель
     */
//    private fun setSpeaker(): Boolean {
//        return try {
//            // Включаем громкоговоритель
//            androidAudioManager.isSpeakerphoneOn = true
//            androidAudioManager.mode = AndroidAudioManager.MODE_NORMAL
//
//            setVolumeForDevice(AudioOutputDevice.SPEAKER)
//
//            _currentAudioDevice.value = AudioOutputDevice.SPEAKER
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }

    /**
     * Полное отключение Bluetooth
     */
    private fun disableBluetoothCompletely() {
        try {
            // Останавливаем Bluetooth SCO
            androidAudioManager.stopBluetoothSco()
            androidAudioManager.isBluetoothScoOn = false

            // Отключаем A2DP
            androidAudioManager.isBluetoothA2dpOn = false

            // Для API 26+ дополнительно отключаем Bluetooth
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                androidAudioManager.mode = AndroidAudioManager.MODE_NORMAL
                // Дополнительные действия для отключения Bluetooth
//            }

            // Небольшая задержка для применения изменений
            Thread.sleep(100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSpeaker(): Boolean {
        return try {
            // Перед переключением отключаем Bluetooth SCO
//            androidAudioManager.stopBluetoothSco()
//            androidAudioManager.isBluetoothScoOn = false


//            disableBluetoothCompletely()
//
//            androidAudioManager.setParameters("audio_force_use=speaker")
//
//            // Устанавливаем режим
//            androidAudioManager.setMode(AndroidAudioManager.MODE_NORMAL)
//
//            // Включаем громкоговоритель
//            androidAudioManager.isSpeakerphoneOn = true
//
//            // Настраиваем громкость
//            setVolumeForDevice(AudioOutputDevice.SPEAKER)
//
//            _currentAudioDevice.value = AudioOutputDevice.SPEAKER

//            switchToPhoneSpeaker()
            switchToPhoneSpeakerApi33()

            true
        } catch (e: Exception) {
            false
        }
    }

    fun switchToPhoneSpeaker() {

        // 1. Отключаем Bluetooth SCO (голосовой канал)
        androidAudioManager.setBluetoothScoOn(false)

        // 2. Отключаем A2DP (музыкальный канал Bluetooth)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Для API 31+ явно отключаем A2DP
            androidAudioManager.stopBluetoothSco()
        } else {
            // Для старых версий — просто снимаем фокус с Bluetooth
            androidAudioManager.isBluetoothA2dpOn = false
        }

        // 3. Включаем громкий динамик телефона
        androidAudioManager.isSpeakerphoneOn = true

        // 4. Устанавливаем режим NORMAL (не IN_CALL/IN_COMMUNICATION)
        androidAudioManager.mode = AndroidAudioManager.MODE_NORMAL


        Log.d("AUDIO", "Переключено на динамик телефона")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun switchToPhoneSpeakerApi33() {
        Log.d("AUDIO", "switchToPhoneSpeakerApi33()")

        // Получаем все доступные устройства вывода
        val devices = androidAudioManager.availableCommunicationDevices


        // Ищем встроенный динамик
        val speaker = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }

        if (speaker != null) {
            // Явно устанавливаем динамик как устройство вывода
            val success = androidAudioManager.setCommunicationDevice(speaker)
            if (success) {
                Log.d("AUDIO", "Выбран динамик телефона (API 33+)")
            } else {
                Log.w("AUDIO", "Не удалось установить динамик через setCommunicationDevice()")
            }
        } else {
            Log.w("AUDIO", "Встроенный динамик не найден в availableCommunicationDevices")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun switchToPhoneEarspicerApi33() {
        Log.d("AUDIO", "switchToPhoneEarspicerApi33()")

        // Получаем все доступные устройства вывода
        val devices = androidAudioManager.availableCommunicationDevices

        // Ищем встроенный динамик
        val speaker = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }

        if (speaker != null) {
            // Явно устанавливаем динамик как устройство вывода
            val success = androidAudioManager.setCommunicationDevice(speaker)
            if (success) {
                Log.d("AUDIO", "Выбран тихий динамик телефона (API 33+)")
            } else {
                Log.w("AUDIO", "Не удалось установить тихий динамик через setCommunicationDevice()")
            }
        } else {
            Log.w("AUDIO", "Встроенный тихий динамик не найден в availableCommunicationDevices")
        }
    }

    /**
     * Переключает на проводные наушники
     */
    private fun setWiredHeadset(): Boolean {
        return try {
            androidAudioManager.isSpeakerphoneOn = false
            androidAudioManager.mode = AndroidAudioManager.MODE_NORMAL

            // Проверяем подключены ли проводные наушники
            if (isWiredHeadsetConnected.value) {
                setVolumeForDevice(AudioOutputDevice.WIRED_HEADSET)
                _currentAudioDevice.value = AudioOutputDevice.WIRED_HEADSET
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Переключает на Bluetooth устройство
     */
    private fun setBluetooth(): Boolean {
        return try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setBluetoothScoApi31()
//            } else {
//                setBluetoothScoLegacy()
//            }
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setBluetoothScoApi31(): Boolean {
        return try {
            // Для API 31+ используем новый метод
            androidAudioManager.mode = AndroidAudioManager.MODE_IN_COMMUNICATION

            // Получаем список доступных устройств
            val devices = androidAudioManager.getDevices(AndroidAudioManager.GET_DEVICES_OUTPUTS)
            val bluetoothDevice = devices.firstOrNull {
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                        it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            }

            if (bluetoothDevice != null) {
                androidAudioManager.isSpeakerphoneOn = false

                androidAudioManager.setCommunicationDevice(bluetoothDevice)

                setVolumeForDevice(AudioOutputDevice.BLUETOOTH)
                _currentAudioDevice.value = AudioOutputDevice.BLUETOOTH
                true
            } else {
                Log.w("AUDIO", "Не получилось установить блютуз")
                false
            }
        } catch (e: Exception) {
            Log.w("AUDIO", "Не удалось установить блютуз")
            false
        }
    }

//    private fun setBluetoothScoLegacy(): Boolean {
//        return try {
//            // Старый метод для версий ниже API 31
//            androidAudioManager.isBluetoothScoOn = true
//            androidAudioManager.startBluetoothSco()
//            androidAudioManager.mode = AndroidAudioManager.MODE_IN_COMMUNICATION
//            androidAudioManager.isSpeakerphoneOn = false
//
//            setVolumeForDevice(AudioOutputDevice.BLUETOOTH)
//
//            _currentAudioDevice.value = AudioOutputDevice.BLUETOOTH
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }

    /**
     * Устанавливает громкость для конкретного устройства
     */
    private fun setVolumeForDevice(device: AudioOutputDevice) {
        val maxVolume = androidAudioManager.getStreamMaxVolume(AndroidAudioManager.STREAM_MUSIC)
        val targetVolume = when (device) {
            AudioOutputDevice.EARPIECE -> (maxVolume * 0.4).toInt() // 40% для ушного динамика
            AudioOutputDevice.SPEAKER -> (maxVolume * 0.4).toInt() // 70% для громкоговорителя
            AudioOutputDevice.WIRED_HEADSET -> (maxVolume * 0.4).toInt() // 60% для наушников
            AudioOutputDevice.BLUETOOTH -> (maxVolume * 0.5).toInt() // 80% для Bluetooth
        }

        androidAudioManager.setStreamVolume(
            AndroidAudioManager.STREAM_MUSIC,
            targetVolume.coerceAtMost(maxVolume),
            0
        )
    }

    /**
     * Настройка мониторинга подключения устройств
     */
    private fun setupAudioDeviceMonitoring() {
        // Мониторинг Bluetooth устройств
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupAudioPlaybackCallback()
        }

        // Обновляем состояние подключенных устройств
        updateConnectedDevices()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupAudioPlaybackCallback() {
        audioPlaybackCallback = object : AudioPlaybackCallback() {
            override fun onPlaybackConfigChanged(configs: MutableList<android.media.AudioPlaybackConfiguration>) {
                super.onPlaybackConfigChanged(configs)
                Log.i("AudioTest", "setupAudioPlaybackCallback - ${configs.map { it }}")
                updateConnectedDevices()
            }
        }

        androidAudioManager.registerAudioPlaybackCallback(audioPlaybackCallback!!, handler)
    }

    /**
     * Обновление информации о подключенных устройствах
     */
    fun updateConnectedDevices() {
        // Проверка Bluetooth
        val isBluetoothA2dpOn = androidAudioManager.isBluetoothA2dpOn
        val isBluetoothScoOn = androidAudioManager.isBluetoothScoOn
        _isBluetoothConnected.value = isBluetoothA2dpOn || isBluetoothScoOn

        // Проверка проводных наушников
        _isWiredHeadsetConnected.value = androidAudioManager.isWiredHeadsetOn

        // Автоматическое переключение при подключении/отключении устройств
        autoSwitchOnDeviceChange()
    }

    /**
     * Автоматическое переключение устройства при изменении подключения
     */
    private fun autoSwitchOnDeviceChange() {
        when {
            _isBluetoothConnected.value -> {
                if (_currentAudioDevice.value != AudioOutputDevice.BLUETOOTH) {
                    setBluetooth()
                }
            }
            _isWiredHeadsetConnected.value -> {
                if (_currentAudioDevice.value != AudioOutputDevice.WIRED_HEADSET) {
                    setWiredHeadset()
                }
            }
            else -> {
                // Если ничего не подключено, используем динамик
                if (_currentAudioDevice.value != AudioOutputDevice.SPEAKER) {
                    setSpeaker()
                }
            }
        }
    }

    /**
     * Обновление текущего устройства на основе системных настроек
     */
    private fun updateCurrentDevice() {
        when {
            androidAudioManager.isBluetoothScoOn || androidAudioManager.isBluetoothA2dpOn -> {
                _currentAudioDevice.value = AudioOutputDevice.BLUETOOTH
            }
            androidAudioManager.isWiredHeadsetOn -> {
                _currentAudioDevice.value = AudioOutputDevice.WIRED_HEADSET
            }
            androidAudioManager.isSpeakerphoneOn -> {
                _currentAudioDevice.value = AudioOutputDevice.SPEAKER
            }
            else -> {
                _currentAudioDevice.value = AudioOutputDevice.EARPIECE
            }
        }
    }

    /**
     * Запрос фокуса аудио
     */
    fun requestAudioFocus(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestAudioFocusOreo()
            } else {
                requestAudioFocusLegacy()
            }
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocusOreo(): Boolean {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        audioFocusRequest = AudioFocusRequest.Builder(AndroidAudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { focusChange ->
                // Обработка изменений фокуса
                when (focusChange) {
                    AndroidAudioManager.AUDIOFOCUS_GAIN -> {
                        // Восстановление воспроизведения
                    }
                    AndroidAudioManager.AUDIOFOCUS_LOSS -> {
                        // Потеря фокуса на длительное время
                    }
                    AndroidAudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        // Временная потеря фокуса
                    }
                }
            }
            .build()

        val result = androidAudioManager.requestAudioFocus(audioFocusRequest!!)
        return result == AndroidAudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun requestAudioFocusLegacy(): Boolean {
        val result = androidAudioManager.requestAudioFocus(
            null,
            AndroidAudioManager.STREAM_MUSIC,
            AndroidAudioManager.AUDIOFOCUS_GAIN
        )
        return result == AndroidAudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    /**
     * Освобождение фокуса аудио
     */
    fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                androidAudioManager.abandonAudioFocusRequest(it)
            }
        } else {
            androidAudioManager.abandonAudioFocus(null)
        }
    }

    /**
     * Получение текущей громкости
     */
    fun getCurrentVolume(): Int {
        return androidAudioManager.getStreamVolume(AndroidAudioManager.STREAM_MUSIC)
    }

    /**
     * Установка громкости
     */
    fun setVolume(volume: Int) {
        val maxVolume = androidAudioManager.getStreamMaxVolume(AndroidAudioManager.STREAM_MUSIC)
        val safeVolume = volume.coerceIn(0, maxVolume)
        androidAudioManager.setStreamVolume(
            AndroidAudioManager.STREAM_MUSIC,
            safeVolume,
            0
        )
    }

    /**
     * Включение/выключение беззвучного режима
     */
    fun setMute(mute: Boolean) {
        androidAudioManager.setStreamMute(AndroidAudioManager.STREAM_MUSIC, mute)
    }

    /**
     * Очистка ресурсов
     */
    fun release() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioPlaybackCallback?.let {
                androidAudioManager.unregisterAudioPlaybackCallback(it)
            }
        }

        abandonAudioFocus()

        // Отключаем Bluetooth SCO если был включен
        if (androidAudioManager.isBluetoothScoOn) {
            androidAudioManager.stopBluetoothSco()
            androidAudioManager.isBluetoothScoOn = false
        }

        // Возвращаем нормальный режим
        androidAudioManager.mode = AndroidAudioManager.MODE_NORMAL
        androidAudioManager.isSpeakerphoneOn = false
    }
}

/**
 * Перечисление доступных устройств вывода звука
 */
enum class AudioOutputDevice {
    EARPIECE,      // Ушной динамик
    SPEAKER,       // Громкоговоритель
    WIRED_HEADSET, // Проводные наушники
    BLUETOOTH      // Bluetooth устройство
}