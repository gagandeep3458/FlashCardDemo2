package com.cuttingedge.flashcardsdemo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cuttingedge.flashcardsdemo.models.Card
import com.cuttingedge.flashcardsdemo.models.CardsAnimateType
import com.cuttingedge.flashcardsdemo.models.CardsOfCategory
import com.cuttingedge.flashcardsdemo.models.Category

class MainViewModel : ViewModel() {

    val categories = mutableStateListOf<Category>()
    val cards = mutableStateListOf<CardsOfCategory>()

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
            cards.add(
                CardsOfCategory(
                    isActive = category.isActive,
                    category = category,
                ).also { cardsOfCategory ->
                    cardsOfCategory.list.addAll(
                        listOf(
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
                    )
                    val animation = when {
                        index < activeIndexForCategory -> CardsAnimateType.FADE_OUT_AND_SLIDE_TO_LEFT
                        index > activeIndexForCategory -> CardsAnimateType.FADE_OUT_AND_RESET_TO_CENTER
                        else -> CardsAnimateType.FADE_IN_AND_RESET_TO_CENTER
                    }
                    cardsOfCategory.animationType = animation
                })
        }
    }

    fun newCategorySelected(oldCategory: Category?, newCategory: Category) {
        if (oldCategory != null) {
            val indexOfOldCategory = categories.indexOf(oldCategory)
            val indexOfNewCategory = categories.indexOf(newCategory)

            // Change Tab Selected
            categories.apply {
                this[indexOfOldCategory] = oldCategory.copy(isActive = false)
                this[indexOfNewCategory] = newCategory.copy(isActive = true)
            }

            cards.apply {

                // Change Cards Displayed
                val indexOfNewSetOfCards = cards.indexOfFirst { it.category.id == newCategory.id }

                // Update prev items positions
                for (i in 0..indexOfNewSetOfCards.minus(1)) {
                    this[i].animationType = CardsAnimateType.FADE_OUT_AND_SLIDE_TO_LEFT
                    this[i] = this[i].copy(isActive = false)
                }

                // Update animation for newly selected cards
                this[indexOfNewSetOfCards].animationType =
                    CardsAnimateType.FADE_IN_AND_RESET_TO_CENTER
                this[indexOfNewSetOfCards] = this[indexOfNewSetOfCards].copy(isActive = true)

                // update next items positions
                if (indexOfNewSetOfCards < this.lastIndex) {
                    for (i in indexOfNewSetOfCards.plus(1)..lastIndex) {
                        this[i].animationType = CardsAnimateType.FADE_OUT_AND_RESET_TO_CENTER
                        this[i] = this[i].copy(isActive = false)
                    }
                }

                // Reset the dragged state for all cards of this category
                val currentCategoryCards = this.first { it.category.id == newCategory.id }
                currentCategoryCards.list.onEach {
                    it.hasBeenDragged = false
                }
            }
        }
    }

    fun cardRemoved(card: Card, cards: CardsOfCategory) {
        val index = cards.list.indexOf(card)
        cards.list[index] = card.copy(hasBeenDragged = true)
    }
}