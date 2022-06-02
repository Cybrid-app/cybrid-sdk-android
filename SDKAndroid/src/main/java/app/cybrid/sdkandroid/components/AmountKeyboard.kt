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

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("MissingPermission")
@Preview
@Composable
public fun AmountKeyboard(modifier: Modifier = Modifier, startAmount: String = "", onAmountChanged: (String) -> Unit = {}) {

  // val context = LocalContext.current
  var amount by rememberSaveable(startAmount) { mutableStateOf(startAmount) }
  val keys = remember { listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "").chunked(3) }
  // val vibrator = remember { context.getSystemService(Vibrator()) }

  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    keys.forEach { rowKeys ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        rowKeys.forEach { key ->
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .clickable {
                amount = when {
                  key.isBlank() -> amount.dropLast(1)
                  amount == "0" && key != "." -> if (key == "0") amount else key
                  amount.endsWith(".") && key == "." -> amount
                  amount.substringAfter(".", "").length == 2 -> amount
                  else -> amount + key
                }.ifBlank { "0" }
                onAmountChanged(amount)
              },
          ) {
            if (key.isBlank()) {
              Icon(Icons.Default.Close, contentDescription = "Remove last digit.")
            } else {
              Text(
                key.ifBlank { "<" },
                // style = AppTypography.h5,
                modifier = Modifier.padding(12.dp)
              )
            }
          }
        }
      }
    }
  }
}
