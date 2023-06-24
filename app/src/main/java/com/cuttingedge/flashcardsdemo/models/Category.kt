package com.cuttingedge.flashcardsdemo.models

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Category(val id: String = UUID.randomUUID().toString(), val index: Int, val name: String, val color: Color, var isActive: Boolean = false)