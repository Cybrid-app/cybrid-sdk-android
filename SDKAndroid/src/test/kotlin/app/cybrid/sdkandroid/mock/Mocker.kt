package app.cybrid.sdkandroid.mock

import app.cybrid.sdkandroid.AppModule
import io.mockk.mockkObject

interface Mocker {

    fun init() { mockkObject(AppModule) }
}