package com.cuttingedge.flashcardsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.cuttingedge.flashcardsdemo.models.CardsAnimateType
import com.cuttingedge.flashcardsdemo.models.CardsOfCategory
import com.cuttingedge.flashcardsdemo.models.Category
import com.cuttingedge.flashcardsdemo.ui.theme.FlashCardsDemoTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlashCardsDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val viewModel by viewModels<MainViewModel>()

                    MainScreen(
                        categories = viewModel.categories,
                        cardsOfCategories = viewModel.cardsOfCategoryList,
                        newCategorySelected = { oldCategory, newCategory ->
                            viewModel.newCategorySelected(oldCategory, newCategory)
                        })
                }
            }
        }
    }

    @OptIn(ExperimentalSwipeableCardApi::class)
    @Composable
    fun MainScreen(
        categories: SnapshotStateList<Category>,
        cardsOfCategories: SnapshotStateList<CardsOfCategory>,
        newCategorySelected: (old: Category?, new: Category) -> Unit
    ) {

        val viewModel by viewModels<MainViewModel>()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {

            // Render Flash Cards
            Box(
                modifier = Modifier
                    .weight(0.85F)
                    .fillMaxSize()
            ) {
                cardsOfCategories.sortedByDescending { it.category.index }.forEach { cards ->

                    val offsetX: Int
                    val alpha: Float

                    when (cards.animationType) {
                        CardsAnimateType.FADE_OUT_AND_SLIDE_TO_LEFT -> {
                            alpha = 0F
                            offsetX = -400
                        }

                        CardsAnimateType.FADE_OUT_AND_RESET_TO_CENTER -> {
                            alpha = 0F
                            offsetX = 0
                        }

                        CardsAnimateType.FADE_IN_AND_RESET_TO_CENTER -> {
                            alpha = 1F
                            offsetX = 0
                        }
                    }

                    val offsetXAnimated by animateDpAsState(
                        targetValue = offsetX.dp,
                        tween(durationMillis = 800)
                    )
                    val alphaAnimated by animateFloatAsState(
                        targetValue = alpha,
                        tween(durationMillis = 800)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = offsetXAnimated)
                            .alpha(alpha = alphaAnimated)

                    ) {
                        cards.list.forEachIndexed { i, card ->
                            if (!card.hasBeenDragged) {

                                val state = rememberSwipeableCardState()

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .swipableCard(state = state, onSwiped = {
                                            viewModel.cardRemoved(card, cards)
                                        })
                                        .padding(
                                            bottom = getBottomPaddingForCard(
                                                i,
                                                cards.list.filter { !it.hasBeenDragged }.size
                                            ),
                                            start = 24.dp,
                                            top = 24.dp,
                                            end = 24.dp
                                        )
                                        .background(
                                            color = Color.DarkGray,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .border(
                                            width = 8.dp,
                                            color = card.color.copy(
                                                alpha = getAlphaForCard(
                                                    i,
                                                    cards.list.filter { !it.hasBeenDragged }.size
                                                )
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = card.name,
                                        style = TextStyle(
                                            fontSize = 24.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    )

                                }
                            }
                        }
                    }
                }
            }

            // Render Categories
            Column(
                modifier = Modifier
                    .weight(0.15F)
                    .fillMaxSize()
            ) {

                val selectedCategory = categories.firstOrNull { it.isActive }

                categories.forEach { category ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(if (category.isActive) 100.dp else 50.dp)
                            .background(color = category.color)
                            .clickable {
                                newCategorySelected.invoke(selectedCategory, category)
                            }, contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.name,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }

                }
            }
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
            else -> 0.dp
        }
    }
}