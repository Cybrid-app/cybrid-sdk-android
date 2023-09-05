package app.cybrid.demoapp.core

import app.cybrid.cybrid_api_id.client.models.PostCustomerTokenIdpModel

object ScopeConstants {

    const val bankTokenScopes = "banks:read banks:write customers:read customers:write customers:execute"
    val customerTokenScopes: List<PostCustomerTokenIdpModel.Scopes> = listOf(
        PostCustomerTokenIdpModel.Scopes.customersColonRead,
        PostCustomerTokenIdpModel.Scopes.customersColonWrite,
        PostCustomerTokenIdpModel.Scopes.accountsColonRead,
        PostCustomerTokenIdpModel.Scopes.accountsColonExecute,
        PostCustomerTokenIdpModel.Scopes.pricesColonRead,
        PostCustomerTokenIdpModel.Scopes.quotesColonRead,
        PostCustomerTokenIdpModel.Scopes.quotesColonExecute,
        PostCustomerTokenIdpModel.Scopes.tradesColonRead,
        PostCustomerTokenIdpModel.Scopes.tradesColonExecute,
        PostCustomerTokenIdpModel.Scopes.transfersColonRead,
        PostCustomerTokenIdpModel.Scopes.transfersColonExecute,
        PostCustomerTokenIdpModel.Scopes.externalBankAccountsColonRead,
        PostCustomerTokenIdpModel.Scopes.externalBankAccountsColonWrite,
        PostCustomerTokenIdpModel.Scopes.externalBankAccountsColonExecute,
        PostCustomerTokenIdpModel.Scopes.workflowsColonRead,
        PostCustomerTokenIdpModel.Scopes.workflowsColonExecute,
        PostCustomerTokenIdpModel.Scopes.externalWalletsColonRead,
        PostCustomerTokenIdpModel.Scopes.externalWalletsColonExecute
    )
}