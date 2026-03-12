package com.vadson40.peripheral.api.model

/**
 * Model for permission request
 *
 * @param microphone doi need to request permission to enable the microphone
 */
data class PermissionRequest(
    val microphone: Boolean = false
)