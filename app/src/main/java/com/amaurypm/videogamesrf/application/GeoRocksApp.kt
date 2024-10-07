package com.amaurypm.videogamesrf.application

import android.app.Application
import com.amaurypm.videogamesrf.data.RockRepository
import com.amaurypm.videogamesrf.data.remote.RetrofitHelper

class GeoRocksApp : Application() {
    private val retrofit by lazy { RetrofitHelper().getRetrofit() }
    val repository by lazy { RockRepository(retrofit) }
}
