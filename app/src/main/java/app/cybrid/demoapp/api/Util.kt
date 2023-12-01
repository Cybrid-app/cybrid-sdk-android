package app.cybrid.demoapp.api

import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.cybrid_api_bank.client.infrastructure.Serializer
import app.cybrid.demoapp.core.App
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class Util {

    companion object {

        fun getIdpSimpleClient() : Retrofit {

            return Retrofit.Builder()
                .baseUrl(App.baseIdpApiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getIdpClient(bearer: String) : Retrofit {

            val okHttpClientBuilder = OkHttpClient()
                .newBuilder()
                .addInterceptor(HttpBearerAuth("Bearer", bearer))
            return Retrofit.Builder()
                .baseUrl(App.baseIdpApiUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Serializer.gsonBuilder.create()))
                .callFactory(okHttpClientBuilder.build())
                .build()
        }

        fun getBankClient(bearer: String) : Retrofit {

            val okHttpClientBuilder = OkHttpClient()
                .newBuilder()
                .addInterceptor(HttpBearerAuth("Bearer", bearer))
            return Retrofit.Builder()
                .baseUrl(App.baseBankApiUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Serializer.gsonBuilder.create()))
                .callFactory(okHttpClientBuilder.build())
                .build()
        }
    }
}