package com.cuttingedge.flashcardsdemo.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.UUID

data class Card(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Color,
    var hasBeenDragged: Boolean = false,
    var alpha: Float = 1F,
    var currentAlpha: Float = 1F,
    var bottomPadding: Dp = 0.dp,
    var currentBottomPadding: Dp = 0.dp
)
