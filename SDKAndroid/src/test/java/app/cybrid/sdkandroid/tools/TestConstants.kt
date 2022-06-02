package app.cybrid.sdkandroid.tools

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel

object TestConstants {

    const val expiredToken = "eyJraWQiOiJsWTdPaTI2SXZaWXhwSXJuUzJsZGp1ODZUWGZxcnlKVDlZNnZVc1hzRUNFIiwiYWxnIjoiUlM1MTIifQ.eyJpc3MiOiJodHRwczovL2lkLmRlbW8uY3licmlkLmFwcCIsImF1ZCI6WyJodHRwczovL2JhbmsuZGVtby5jeWJyaWQuYXBwIiwiaHR0cHM6Ly9hcGktaW50ZXJuYWwta2V5Lmhlcm9rdWFwcC5jb20iLCJodHRwczovL2lkLmRlbW8uY3licmlkLmFwcCIsImh0dHBzOi8vYXBpLWludGVybmFsLWFjY291bnRzLmhlcm9rdWFwcC5jb20iLCJodHRwczovL2FwaS1pbnRlcm5hbC1pZGVudGl0eS5oZXJva3VhcHAuY29tIiwiaHR0cHM6Ly9hcGktaW50ZWdyYXRpb24tZXhjaGFuZ2UuaGVyb2t1YXBwLmNvbSJdLCJzdWIiOiIzYWEyYmM0ODdhZTQ2OWJiYzAzY2I0MmNmNzJlM2FhMyIsInN1Yl90eXBlIjoiYmFuayIsInNjb3BlIjpbImJhbmtzOnJlYWQiLCJiYW5rczp3cml0ZSIsImFjY291bnRzOnJlYWQiLCJhY2NvdW50czpleGVjdXRlIiwiY3VzdG9tZXJzOnJlYWQiLCJjdXN0b21lcnM6d3JpdGUiLCJjdXN0b21lcnM6ZXhlY3V0ZSIsInByaWNlczpyZWFkIiwicXVvdGVzOmV4ZWN1dGUiLCJ0cmFkZXM6ZXhlY3V0ZSIsInRyYWRlczpyZWFkIl0sImlhdCI6MTY1MTg1NDcwNywiZXhwIjoxNjUxODU2NTA3LCJqdGkiOiJkMzcxNjc0ZC0wOTgxLTRkZDUtYjc2YS02ZTFiOTQ4MTk4N2YiLCJ0b2tlbl90eXBlIjoiYWNjZXNzIiwicHJvcGVydGllcyI6eyJ0eXBlIjoic2FuZGJveCJ9fQ.LM8OBLOLHsCuWx4XNHjIHmvmuyZBviO_cYQ-WTqjiszpL0dVuC_z6v9FJ1IwVO8SxBhwH3Ut1aVE3kTapHLOmN6c6ca7y-fURPl8ojxAvs0eDMkT-HYD6QUHZ7ylrppa0w78Q3gOObytELGbFTpJ_QYd8NE1eewsoDeNKDc9p-RhGHoamAjHfnJcC4zOyXKjIr_Etn5RgA4xmYwxe8887vC9rPo9vuOou71gWufNrgqNyUBpuiDpAgTw4zhNynlJBXtpL7kNCaKHn5D8qRUqJyQzSZ1jctmzFLls-hNbC58wb8t0vPAYvVsgzQOYDiLKsJMUBJ4IxAiQbfKVe3HpdobqXCu_7fborK7MB2Q2Msx-YMby9DFseOjXV6i-pNYRxvLIVE1hZkjp15-edsNclTGpIngIGFWGp2Tzwd3m8PG-9WELWRufZCbTlwrzuIVwpCRXdP-4Zt_VP7cXhHKWJQmKFLKJ_WSzKvwqaL_-aMpb6rihv4rjeDuFTtrgGDbx0TXYMAjjIdRoPm9PuAwnmclnASUdBBAHWCXu0V96AUJygn9QQZ-1ndlaQCpQ3Z6URLkGnv5-9MDT8GLTbC6tTzopHYNztNiJ-Z8gQtLJS8lSof3DrhJWgpmyd_OqSwYzeuiiz9lt5haUJnSy8QxFh7tWaS5-wCkcRqy_qAUzZOQ"

    val BTC_ASSET: AssetBankModel = AssetBankModel(
        code = "BTC",
        decimals = 8,
        name = "Bitcoin",
        symbol = "₿",
        type= AssetBankModel.Type.crypto,
    )

    val ETH_ASSET: AssetBankModel = AssetBankModel(
        code = "ETH",
        decimals = 18,
        name = "Ethereum",
        symbol = "Ξ",
        type= AssetBankModel.Type.crypto,
    )

    val CAD_ASSET: AssetBankModel = AssetBankModel(
        code = "CAD",
        decimals = 2,
        name = "Canadian Dollar",
        symbol = "$",
        type= AssetBankModel.Type.fiat,
    )
}