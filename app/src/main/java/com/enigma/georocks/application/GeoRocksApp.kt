package com.enigma.georocks.application

import android.app.Application
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.RetrofitHelper
import com.google.firebase.FirebaseApp

class GeoRocksApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    private val retrofit by lazy { RetrofitHelper().getRetrofit() }
    val repository by lazy { RockRepository(retrofit) }
}
