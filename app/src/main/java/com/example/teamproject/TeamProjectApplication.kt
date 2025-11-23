package com.example.teamproject

import android.app.Application

class TeamProjectApplication : Application() {

    companion object {
        lateinit var instance: TeamProjectApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
