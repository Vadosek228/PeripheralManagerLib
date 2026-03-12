package com.vadson40.peripherymanager

import android.app.Application
import com.vadson40.peripherymanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

/**
 * @author Akulinin Vladislav
 * @since 02.03.2026
 */
class AppExample : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AppExample)
            modules(appModule)
        }
    }
}