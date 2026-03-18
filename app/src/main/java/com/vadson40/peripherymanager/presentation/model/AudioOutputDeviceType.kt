package com.vadson40.peripherymanager.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
enum class AudioOutputDeviceType(
    val title: String,
    val icon: ImageVector
) {
    EARPIECE(
        title = "Ушной динамик",
        icon = Icons.Default.Build
    ),

    SPEAKER(
        title = "Громкоговоритель",
        icon = Icons.Default.Favorite
    ),

    WIRED_HEADSET(
        title = "Проводные наушники",
        icon = Icons.Default.Face
    ),

    BLUETOOTH(
        title = "Bluetooth устройство",
        icon = Icons.Default.Edit
    ),

    UNKNOWN(
        title = "Неизвестный тип",
        icon = Icons.Default.Clear);
}