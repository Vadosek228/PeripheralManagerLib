package com.vadson40.peripherymanager.di

import com.vadson40.peripheral.api.InitPeripheralLib
import com.vadson40.peripheral.api.PeripheralManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<PeripheralManager> {
        InitPeripheralLib.init(androidContext())
    }
}