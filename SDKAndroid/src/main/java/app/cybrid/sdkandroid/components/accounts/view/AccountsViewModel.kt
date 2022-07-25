package app.cybrid.sdkandroid.components.accounts.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.AccountsApi
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {

    var accounts:List<AccountBankModel> by mutableStateOf(listOf())

    fun getAccounts() {

        val accountService = AppModule.getClient().createService(AccountsApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.launch {

                    // -- Getting prices
                    val accountsResult = getResult { accountService.listAccounts(customerGuid = Cybrid.instance.customerGuid) }
                    accountsResult.let {
                        accounts = if (isSuccessful(it.code ?: 500)) {
                            it.data?.objects ?: listOf()
                        } else {
                            Logger.log(LoggerEvents.DATA_ERROR, "Accounts Component - Data :: ${it.message}")
                            listOf()
                        }
                    }
                }
            }
        }
    }
}