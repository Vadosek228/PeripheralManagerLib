package com.vadson40.peripherymanager.di

import com.vadson40.peripheral.api.PeripheralManager
import com.vadson40.peripheral.impl.PeripheralManagerFactory
import com.vadson40.peripheral.impl.permission.PermissionManager
import com.vadson40.peripheral.impl.permission.PermissionManagerImpl
import org.koin.dsl.module

val appModule = module {

//    single<PermissionManager> { PermissionManagerImpl(get()) }
//    single<PeripheralManager> { PeripheralManagerFactory.create(get(), get(), get()) }

}