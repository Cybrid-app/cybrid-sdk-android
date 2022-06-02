package app.cybrid.demoapp.api

import app.cybrid.demoapp.core.App
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Util {

    companion object {

        fun getClient() : Retrofit {
            return Retrofit.Builder()
                .baseUrl(App.demoUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}