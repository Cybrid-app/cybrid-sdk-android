package app.cybrid.demoapp.core

import app.cybrid.cybrid_api_id.client.models.PostCustomerTokenIdpModel

object ScopeConstants {

    const val bankTokenScopes = "banks:read banks:write accounts:read accounts:execute customers:read customers:write customers:execute prices:read quotes:execute trades:execute trades:read workflows:execute workflows:read external_bank_accounts:execute external_bank_accounts:read external_bank_accounts:write transfers:read transfers:execute"
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

    )
}