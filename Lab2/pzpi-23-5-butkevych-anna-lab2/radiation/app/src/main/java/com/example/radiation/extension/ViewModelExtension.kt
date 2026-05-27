package com.example.radiation.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
@Composable
inline fun <reified VM : ViewModel> getViewModel(): VM {
    return hiltViewModel()
}

