package com.gsnamespace.atforecast.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeScreen(
    onNavigateToDetails: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold {
        Column(
            modifier = Modifier.padding(it)
        ) {
            ElevatedButton(
                onClick = onNavigateToDetails
            ) {
                Text("Home Screen")
            }
        }
    }
}