package com.vadson40.peripherymanager.model

/**
 * @author Akulinin Vladislav
 * @since 05.02.2026
 */
/**
 * Перечисление доступных устройств вывода звука
 */
enum class AudioOutputDevice(val title: String) {
    EARPIECE(title = "Ушной динамик"),      // Ушной динамик
    SPEAKER(title = "Громкоговоритель"),       // Громкоговоритель
    WIRED_HEADSET(title = "Проводные наушники"), // Проводные наушники
    BLUETOOTH(title = "Bluetooth устройство")     // Bluetooth устройство
}