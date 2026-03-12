package com.vadson40.peripheral.impl.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Implementation of [com.vadson40.peripheral.impl.permission.PermissionManager].
 *
 * @author Akulinin Vladislav
 * @since 12.03.2026
 */
class PermissionManagerImpl(
    private val appContext: Context
) : PermissionManager {

    override fun isMicrophonePermissionGranted(): Boolean {
        return appContext.isPermissionGranted(Manifest.permission.RECORD_AUDIO)
    }

    override fun isCameraPermissionGranted(): Boolean {
        return appContext.isPermissionGranted(Manifest.permission.CAMERA)
    }

    private fun Context.isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}