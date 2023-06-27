package com.cuttingedge.flashcardsdemo.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID

data class CardsOfCategory(
    val id: String = UUID.randomUUID().toString(),
    val list: SnapshotStateList<Card> = mutableStateListOf(),
    val isActive: Boolean = false,
    val category: Category,
    var isVisible: Boolean = false,
    var animationType: CardsAnimateType = CardsAnimateType.FADE_IN_AND_RESET_TO_CENTER
)

enum class CardsAnimateType {
    FADE_OUT_AND_SLIDE_TO_LEFT,
    FADE_OUT_AND_RESET_TO_CENTER,
    FADE_IN_AND_RESET_TO_CENTER
}