package com.vadson40.peripherymanager.di

import com.vadson40.peripheral.api.InitPeripheralLib
import com.vadson40.peripheral.api.PeripheralManager
import com.vadson40.peripherymanager.domain.manager.MediaPlayerManager
import com.vadson40.peripherymanager.domain.manager.MediaPlayerManagerImpl
import com.vadson40.peripherymanager.presentation.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<PeripheralManager> {
        InitPeripheralLib.init(androidContext())
    }
    single<MediaPlayerManager> { MediaPlayerManagerImpl(androidContext()) }
    viewModel {
        MainViewModel(
            get(),
            get()
        )
    }
}