package app.cybrid.demoapp.api

import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.demoapp.core.App
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Util {

    companion object {

        fun getIdpClient() : Retrofit {

            return Retrofit.Builder()
                .baseUrl(App.baseIdpApiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addInterceptor(HttpBearerAuth("Bearer", this.bearer))
                .build()
        }

        fun getBankClient() : Retrofit {
            return Retrofit.Builder()
                .baseUrl(App.baseBankApiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addInterceptor(HttpBearerAuth("Bearer", this.bearer))
                .build()
        }
    }
}