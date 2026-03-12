package com.vadson40.peripheral.api

/**
 * User permission manager. Check available permissions for users.
 *
 * @author Akulinin Vladislav
 * @since 12.03.2026
 */
interface PermissionManager {
    fun isMicrophonePermissionGranted(): Boolean
    fun isCameraPermissionGranted(): Boolean
}