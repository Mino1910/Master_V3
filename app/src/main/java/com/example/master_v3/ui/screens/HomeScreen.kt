package com.example.master_v3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.master_v3.ui.navigation.ARScreen

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {navController.navigate(ARScreen)},
            colors = ButtonColors(
                contentColor = Color.Black,
                containerColor = Color.Red,
                disabledContainerColor = Color.Red,
                disabledContentColor = Color.Black
            ),
            modifier = Modifier.size(150.dp, 50.dp)
        )
        {
            Text(text = "Start")
        }
    }
}