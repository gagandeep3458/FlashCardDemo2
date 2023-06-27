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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import kotlin.math.abs

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

        val screenWidth = with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx()
        }

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

                        val lastIndex = cards.list.filter { !it.hasBeenDragged }.lastIndex

                        cards.list.forEachIndexed { i, card ->
                            if (!card.hasBeenDragged) {

                                val state = rememberSwipeableCardState()

                                if (i > 0) {
                                    // Recalculate previous card's alpha and bottom padding
                                    // based on how much current card has slide

                                    val xDragValue =
                                        abs(state.offset.value.x.div(screenWidth.times(1.5F)))


                                    if (lastIndex.minus(i) == 0 && lastIndex >= 1) {
                                        cards.list[i.minus(1)].currentAlpha =
                                            getAlphaFromDragValue(
                                                cards.list[i.minus(1)].alpha,
                                                cards.list[i.minus(0)].alpha,
                                                xDragValue
                                            )
                                        cards.list[i.minus(1)].currentBottomPadding =
                                            getDpFromDragValue(
                                                cards.list[i.minus(1)].bottomPadding.value,
                                                cards.list[i.minus(0)].bottomPadding.value,
                                                xDragValue
                                            )
                                        cards.list[i.minus(1)].currentTopPadding =
                                            getDpFromDragValue(
                                                cards.list[i.minus(1)].topPadding.value,
                                                cards.list[i.minus(0)].topPadding.value,
                                                xDragValue
                                            )
                                    }

                                    if (lastIndex.minus(i.minus(1)) == 1 && lastIndex >= 2) {
                                        cards.list[i.minus(2)].currentAlpha =
                                            getAlphaFromDragValue(
                                                cards.list[i.minus(2)].alpha,
                                                cards.list[i.minus(1)].alpha,
                                                xDragValue
                                            )
                                        cards.list[i.minus(2)].currentBottomPadding =
                                            getDpFromDragValue(
                                                cards.list[i.minus(2)].bottomPadding.value,
                                                cards.list[i.minus(1)].bottomPadding.value,
                                                xDragValue
                                            )
                                        cards.list[i.minus(2)].currentTopPadding =
                                            getDpFromDragValue(
                                                cards.list[i.minus(2)].topPadding.value,
                                                cards.list[i.minus(1)].topPadding.value,
                                                xDragValue
                                            )
                                    }

                                    if (lastIndex.minus(i.minus(2)) == 2 && lastIndex >= 3) {
                                        cards.list[i.minus(3)].currentAlpha =
                                            getAlphaFromDragValue(
                                                cards.list[i.minus(3)].alpha,
                                                cards.list[i.minus(2)].alpha,
                                                xDragValue
                                            )
                                        cards.list[i.minus(3)].currentBottomPadding =
                                            getDpFromDragValue(
                                                cards.list[i.minus(3)].bottomPadding.value,
                                                cards.list[i.minus(2)].bottomPadding.value,
                                                xDragValue
                                            )
                                        cards.list[i.minus(3)].currentTopPadding =
                                            getDpFromDragValue(
                                                cards.list[i.minus(3)].topPadding.value,
                                                cards.list[i.minus(2)].topPadding.value,
                                                xDragValue
                                            )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .swipableCard(state = state, onSwiped = {
                                            viewModel.cardRemoved(card, cards)
                                        })
                                        .padding(
                                            bottom = card.currentBottomPadding,
                                            start = 24.dp,
                                            top = card.currentTopPadding,
                                            end = 24.dp
                                        )
                                        .background(
                                            color = Color.DarkGray.copy(
                                                alpha = card.currentAlpha
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .border(
                                            width = 8.dp,
                                            color = card.color.copy(
                                                alpha = card.currentAlpha
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    if (i >= lastIndex.minus(2)) {

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

    private fun getDpFromDragValue(startDp: Float, endDp: Float, interpolationValue: Float): Dp {
        val clampedInterpolationValue = interpolationValue.coerceIn(0F, 1F)
        return (startDp + (endDp - startDp) * clampedInterpolationValue).toInt().dp
    }

    private fun getAlphaFromDragValue(
        startAlpha: Float,
        endAlpha: Float,
        interpolationValue: Float
    ): Float {
        val clampedInterpolationValue = interpolationValue.coerceIn(0F, 1F)
        return startAlpha + (endAlpha - startAlpha) * clampedInterpolationValue
    }
}