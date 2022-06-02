/*
 * Cybrid-SDK
 * Copyright (c) 2022 Cybrid Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cybrid.sdkandroid.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AmountKeyboardUITest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun buttonsTest() {

    // -- Given
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0")

    // -- When
    composeTestRule.setContent {
      AmountKeyboard()
    }

    // -- Then
    for (key in keys) {
      composeTestRule.onNodeWithText(key).assertIsDisplayed()
    }
  }

  @Test
  fun buttonsClickTest() {

    // -- Given
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0")
    var amountSet = ""
    var checkSet = ""

    // -- When
    composeTestRule.setContent {
      AmountKeyboard(onAmountChanged = { amount -> amountSet = amount })
    }

    // -- Then
    for (key in keys) {

      composeTestRule.onNodeWithText(key).performClick()
      checkSet += key
      assertEquals(checkSet, amountSet)
    }
  }
}