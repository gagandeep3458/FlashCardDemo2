package com.cuttingedge.flashcardsdemo.models

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Card(val id: String = UUID.randomUUID().toString(), val name: String, val color: Color, var isActive: Boolean = false)
