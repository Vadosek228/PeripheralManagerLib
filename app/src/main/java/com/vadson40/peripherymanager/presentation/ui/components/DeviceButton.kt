package com.vadson40.peripherymanager.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vadson40.peripherymanager.presentation.model.AudioOutputDeviceType

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
@Composable
fun DeviceButton(
    device: AudioOutputDeviceType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors =
        if (selected) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        else ButtonDefaults.buttonColors()

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = selected,
        colors = colors
    ) {
        Icon(
            imageVector = device.icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(device.title)
    }
}