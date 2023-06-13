package app.cybrid.demoapp.ui.util

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule

class ComposeHelpers {}

fun ComposeTestRule.waitUntilNodeCount(
    matcher: SemanticsMatcher,
    count: Int,
    timeoutMillis: Long = 3_000L
) {
    this.waitUntil(timeoutMillis) {
        this.onAllNodes(matcher).fetchSemanticsNodes().size == count
    }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 3_000L
) {
    return this.waitUntilNodeCount(matcher, 1, timeoutMillis)
}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilDoesNotExist(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 3_000L
) {
    return this.waitUntilNodeCount(matcher, 0, timeoutMillis)
}