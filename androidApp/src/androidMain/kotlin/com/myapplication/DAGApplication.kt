package com.myapplication

import android.app.Application
import di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DAGApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DAGApplication)
            androidLogger()
            modules(commonModule)
        }
    }
}