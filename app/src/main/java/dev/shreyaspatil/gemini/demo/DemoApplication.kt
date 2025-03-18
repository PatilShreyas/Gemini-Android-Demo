package dev.shreyaspatil.gemini.demo

import android.app.Application

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AssistantService.init(this)
    }
}