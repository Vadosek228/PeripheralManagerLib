package com.vadson40.peripherymanager.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
@Composable
fun AudioPlayerContent(
    playClick: () -> Unit,
    stopClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = playClick,
            content = { Text("media start") }
        )
        Spacer(modifier = Modifier.padding(start = 24.dp))
        Button(
            onClick = stopClick,
            content = { Text("media stop") }
        )
    }
}