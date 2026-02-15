package com.example.stormwatch

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    object HomeScreen : Screens()
}