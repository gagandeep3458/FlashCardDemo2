package com.cuttingedge.flashcardsdemo.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.UUID

data class CardsOfCategory(
    val id: String = UUID.randomUUID().toString(),
    val list: SnapshotStateList<Card> = mutableStateListOf(),
    val isActive: Boolean = false,
    val category: Category,
    var isVisible: Boolean = false,
    var currentOffsetX: Dp = 0.dp,
    var currentAlpha: Float = 1F,
    var animationDurationMillis: Float = 800F
)