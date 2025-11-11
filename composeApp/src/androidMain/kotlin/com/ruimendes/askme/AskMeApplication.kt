package com.ruimendes.askme

import android.app.Application
import com.ruimendes.askme.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AskMeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AskMeApplication)
            androidLogger()
        }
    }
}