package com.rarilabs.rarime.modules.home.v2

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CircledBadgeWithCounter
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlin.math.abs

data class CardContent(
    val properties: CardProperties,
    val onCardClick: () -> Unit = {},
    val footer: @Composable () -> Unit
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val cardContent = remember {
        listOf(
            CardContent(
                properties = CardProperties(
                    header = "Your Device",
                    subTitle = "Your Identity",
                    icon = R.drawable.ic_rarime,
                    image = R.drawable.drawable_hand_phone,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9AFE8A), Color(0xFF8AFECC)
                        )
                    )
                ),
                onCardClick = {},
                footer = {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("Hello World")
                    }
                }
            ),
            CardContent(
                properties = CardProperties(
                    header = "Invite",
                    subTitle = "Others",
                    icon = R.drawable.ic_rarimo,
                    image = R.drawable.invite_groupe_image,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFCBE7EC), Color(0xFFF2F8EE)
                        )
                    )
                ),
                onCardClick = {},
                footer = {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RarimeTheme.colors.baseWhite)
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "149250-1596", style = RarimeTheme.typography.h5)
                            VerticalDivider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .padding(horizontal = 16.dp)
                            )
                            AppIcon(id = R.drawable.ic_copy_simple)
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(text = "*Nothing leaves thi devise")
                    }
                }
            ),
            CardContent(
                properties = CardProperties(
                    header = "An Unforgettable",
                    subTitle = "Wallet",
                    icon = R.drawable.ic_rarime,
                    image = R.drawable.no_more_seed_image,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF6F3D6),
                            Color(0xFFBCEB3D)
                        )
                    )
                ),
                onCardClick = {},
                footer = {
                    TransparentButton(
                        modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, start = 12.dp, end = 12.dp),
                        onClick = {},
                        text = "Join early waitlist"
                    )

                }
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { cardContent.size })

    Column(modifier = modifier.fillMaxSize()) {

        Row(
            Modifier.padding(start = 20.dp, top = 26.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hi Stranger", style = RarimeTheme.typography.h5)
            Spacer(modifier = Modifier.weight(1f))
            CircledBadgeWithCounter(
                modifier = Modifier.clickable { },
                iconId = R.drawable.ic_bell,
                containerSize = 40,
                containerColor = RarimeTheme.colors.backgroundPrimary,
                contentSize = 20,
                badgeSize = 16,
                contentColor = RarimeTheme.colors.textPrimary
            )
        }

        // Основной контент с карточками
        Column(modifier = Modifier.padding(start = 22.dp, end = 22.dp)) {
            VerticalPager(
                state = pagerState,
                contentPadding = PaddingValues(top = 63.dp, bottom = 100.dp)
            ) { page ->
                // Вычисляем смещение страницы относительно центральной
                val pageOffset =
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                // Ограничиваем смещение диапазоном [0, 1]
                val absoluteOffset = abs(pageOffset).coerceIn(0f, 1f)
                // Интерполируем масштаб: если страница в центре (offset == 0) → scale = 1, иначе → scale = 0.8
                val targetScale = lerp(0.8f, 1f, 1f - absoluteOffset)

                // Анимируем изменение масштаба с bounce-эффектом
                val scale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = spring(
                        dampingRatio = 0.5f, // немного «отскока»
                        stiffness = 300f
                    )
                )

                HomeCard(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                    cardProperties = cardContent[page].properties,
                    footer = cardContent[page].footer,
                    onCardClick = cardContent[page].onCardClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    Surface {
        HomeScreen()
    }
}