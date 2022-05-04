package com.nonamepk.levantalo.screens.start

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonamepk.levantalo.R

@Composable
fun StartScreen(
    onSearchItemsClick: () -> Unit,
    onTakeOutItemsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿What do you want to do?",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSearchItemsClick
            ) {
                Text(text = stringResource(id = R.string.search_items_button))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onTakeOutItemsClick
            ) {
                Text(text = stringResource(id = R.string.gift_items_button))
            }
        }
    }
}