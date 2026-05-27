package com.example.radiation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NestedGraph {
    @Serializable
    data object Auth : NestedGraph()
    @Serializable
    data object Main : NestedGraph()
}

