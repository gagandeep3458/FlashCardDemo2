package com.cuttingedge.flashcardsdemo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.cuttingedge.flashcardsdemo.models.Card
import com.cuttingedge.flashcardsdemo.models.CardsOfCategory
import com.cuttingedge.flashcardsdemo.models.Category

class MainViewModel : ViewModel() {

    val categories = mutableStateListOf<Category>()
    val cardsOfCategoryList = mutableStateListOf<CardsOfCategory>()

    init {

        val initiallySelectedIndex = 0

        // Populate Categories Initially
        categories.addAll(
            listOf(
                Category(index = 0, name = "A", color = Color.Red),
                Category(index = 1, name = "B", color = Color.Blue),
                Category(index = 2, name = "C", color = Color.Green),
                Category(index = 3, name = "D", color = Color.Yellow),
            ).also {
                it[initiallySelectedIndex].isActive = true
            }
        )


        val activeIndexForCategory: Int = categories.firstOrNull { it.isActive }?.index ?: 0

        // Populate Cards for each Category

        categories.forEachIndexed { index, category ->
            cardsOfCategoryList.add(
                CardsOfCategory(
                    isActive = category.isActive,
                    category = category,
                ).also { cardsOfCategory ->
                    val list = listOf(
                        Card(
                            name = "${category.name} Card 1",
                            color = category.color
                        ),
                        Card(
                            name = "${category.name} Card 2",
                            color = category.color
                        ),
                        Card(
                            name = "${category.name} Card 3",
                            color = category.color
                        ),
                        Card(
                            name = "${category.name} Card 4",
                            color = category.color
                        ),
                        Card(
                            name = "${category.name} Card 5",
                            color = category.color,
                        )
                    )
                    cardsOfCategory.list.addAll(
                        list.onEachIndexed { i, c ->
                            c.alpha = getAlphaForCard(i, list.size)
                            c.currentAlpha = c.alpha
                            c.bottomPadding = getBottomPaddingForCard(i, list.size)
                            c.topPadding = getTopPaddingForCard(i, list.size)
                            c.currentBottomPadding = c.bottomPadding
                            c.currentTopPadding = c.topPadding
                        }
                    )
                    cardsOfCategory.apply {
                        when {
                            index < activeIndexForCategory -> {
                                this.currentAlpha = 0F
                                this.currentOffsetX = (-400).dp
                            }

                            index > activeIndexForCategory -> {
                                this.currentAlpha = 0F
                                this.currentOffsetX = (0).dp
                            }

                            else -> {
                                this.currentAlpha = 1F
                                this.currentOffsetX = (0).dp
                            }
                        }
                    }
                })
        }

        cardsOfCategoryList[initiallySelectedIndex].isVisible = true
        val indexOfUpcomingCategory = if (initiallySelectedIndex == categories.size.minus(1)) {
            0
        } else {
            initiallySelectedIndex.plus(1)
        }
        cardsOfCategoryList[indexOfUpcomingCategory].isVisible = true
    }

    fun newCategorySelected(oldCategory: Category?, newCategory: Category) {
        if (oldCategory != null) {
            val indexOfOldCategory = categories.indexOfFirst { it.id == oldCategory.id }
            val indexOfNewCategory = categories.indexOfFirst { it.id == newCategory.id }

            // Change Tab Selected
            categories.apply {
                this[indexOfOldCategory] = this[indexOfOldCategory].copy(isActive = false)
                this[indexOfNewCategory] = this[indexOfNewCategory].copy(isActive = true)
            }

            cardsOfCategoryList.apply {

                // Change Cards Displayed
                val indexOfOldSetOfCards =
                    cardsOfCategoryList.indexOfFirst { it.category.id == oldCategory.id }
                val indexOfNewSetOfCards =
                    cardsOfCategoryList.indexOfFirst { it.category.id == newCategory.id }
                val indexOfUpcomingSetOfCards =
                    if (indexOfNewSetOfCards == categories.size.minus(1)) {
                        0
                    } else {
                        indexOfNewSetOfCards.plus(1)
                    }

                if (indexOfUpcomingSetOfCards == 0) {
                    cardsOfCategoryList[0].list.onEach { it.hasBeenDragged = false }
                }

                // Update prev items positions
                for (i in 0..indexOfNewSetOfCards.minus(1)) {
                    this[i].apply {
                        this.currentAlpha = 0F
                        this.currentOffsetX = (-400).dp
                        this.animationDurationMillis = 800F
                        this.isVisible = i == indexOfOldSetOfCards || i == indexOfUpcomingSetOfCards
                    }
                    this[i] = this[i].copy(isActive = false)
                }

                // Update animation for newly selected cards
                this[indexOfNewSetOfCards].apply {
                    this.currentAlpha = 1F
                    this.currentOffsetX = (0).dp
                    this.animationDurationMillis = 800F
                    this.isVisible = true
                }
                this[indexOfNewSetOfCards] = this[indexOfNewSetOfCards].copy(isActive = true)

                // update next items positions
                if (indexOfNewSetOfCards < this.lastIndex) {
                    for (i in indexOfNewSetOfCards.plus(1)..lastIndex) {
                        this[i].apply {
                            this.currentAlpha = 0F
                            this.currentOffsetX = (0).dp
                            this.animationDurationMillis = 800F
                            this.isVisible =
                                i == indexOfOldSetOfCards || i == indexOfUpcomingSetOfCards
                        }
                        this[i] = this[i].copy(isActive = false)
                    }
                }

                // Reset the dragged state for all cards of this category
                val currentCategoryCards = this.first { it.category.id == newCategory.id }
                currentCategoryCards.list.onEachIndexed { i, c ->
                    c.hasBeenDragged = false
                    c.alpha = getAlphaForCard(i, currentCategoryCards.list.size)
                    c.currentAlpha = c.alpha
                    c.bottomPadding = getBottomPaddingForCard(i, currentCategoryCards.list.size)
                    c.topPadding = getTopPaddingForCard(i, currentCategoryCards.list.size)
                    c.currentBottomPadding = c.bottomPadding
                    c.currentTopPadding = c.topPadding
                }
            }
        }
    }

    fun cardRemoved(card: Card, cards: CardsOfCategory) {
        val index = cards.list.indexOf(card)
        cards.list[index] = card.copy(hasBeenDragged = true)

        // Recalculate alpha and bottom padding of cards
        val newList = cards.list.filter { !it.hasBeenDragged }
        newList.onEachIndexed { i, c ->
            c.alpha = getAlphaForCard(i, newList.size)
            c.currentAlpha = c.alpha
            c.bottomPadding = getBottomPaddingForCard(i, newList.size)
            c.topPadding = getTopPaddingForCard(i, newList.size)
            c.currentBottomPadding = c.bottomPadding
            c.currentTopPadding = c.topPadding
        }

        // If all cards have been dragged of this category then switch to next category
        if (cards.list.none { !it.hasBeenDragged }) {
            val sizeOfCategories = cardsOfCategoryList.size
            val indexOfCurrentCategory = cardsOfCategoryList.indexOfFirst { it.isActive }
            val indexOfNewCategory = if (indexOfCurrentCategory == sizeOfCategories.minus(1)) {
                0
            } else {
                indexOfCurrentCategory.plus(1)
            }

            newCategorySelected(
                cardsOfCategoryList[indexOfCurrentCategory].category,
                cardsOfCategoryList[indexOfNewCategory].category
            )
        }
    }

    private fun getAlphaForCard(i: Int, size: Int): Float {
        return when (i) {
            size.minus(1) -> 1F
            size.minus(2) -> 0.5F
            size.minus(3) -> 0.2F
            else -> 0F
        }
    }

    private fun getBottomPaddingForCard(i: Int, size: Int): Dp {
        return when (i) {
            size.minus(1) -> 60.dp
            size.minus(2) -> 30.dp
            size.minus(3) -> 0.dp
            else -> 0.dp
        }
    }

    private fun getTopPaddingForCard(i: Int, size: Int): Dp {
        return when (i) {
            size.minus(1) -> 0.dp
            size.minus(2) -> 30.dp
            size.minus(3) -> 60.dp
            else -> 60.dp
        }.plus(24.dp)
    }
}