package com.vadson40.peripheral.impl

import android.content.Context
import android.os.Build
import com.vadson40.peripheral.api.PeripheralManager
import com.vadson40.peripheral.api.PermissionManager
import com.vadson40.peripheral.impl.utils.Log
import com.vadson40.peripheral.impl.utils.Log.i
import com.vadson40.peripheral.impl.utils.TAG_PERIPHERAL
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Factory for creating [PeripheralManager].
 *
 * @author Vladislav Akulinin
 * @since 25.02.2026
 */
internal object PeripheralManagerFactory {
    fun create(
        context: Context,
        dispatcher: CoroutineDispatcher,
        permissionManager: PermissionManager
    ): PeripheralManager {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                Log.tag(TAG_PERIPHERAL).i("During initialization, a PeripheralManagerSImpl was created")
                PeripheralManagerSImpl(
                    context = context,
                    dispatcher = dispatcher,
                    permissionManager = permissionManager
                )
            }
            else -> {
                Log.tag(TAG_PERIPHERAL).i("During initialization, a PeripheralManagerOImpl was created")
                PeripheralManagerOImpl(
                    context = context,
                    dispatcher = dispatcher,
                    permissionManager = permissionManager
                )
            }
        }
    }
}