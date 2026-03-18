package com.vadson40.peripherymanager.model

/**
 * Перечисление доступных устройств вывода звука
 */
enum class AudioOutputDevice(val title: String) {
    EARPIECE(title = "Ушной динамик"),
    SPEAKER(title = "Громкоговоритель"),
    WIRED_HEADSET(title = "Проводные наушники"),
    BLUETOOTH(title = "Bluetooth устройство")
}