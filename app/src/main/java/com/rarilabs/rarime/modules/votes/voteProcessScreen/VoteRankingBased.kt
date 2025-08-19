package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_RANKING_BASED_VOTE_ITEM
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.api.voting.models.PollResult
import com.rarilabs.rarime.api.voting.models.Question
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun VoteRankingBasedScreen(
    modifier: Modifier = Modifier,
    selectedPoll: Poll,
    onBackClick: () -> Unit,
    onVote: (List<PollResult>) -> Unit
) {
    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ranking based voting")
            BaseIconButton(
                onClick = { onBackClick.invoke() },
                icon = R.drawable.ic_close,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.componentPrimary,
                    contentColor = RarimeTheme.colors.textPrimary
                ),
            )
        }

        VoteRankingCard(
            voteOption = selectedPoll.questionList[0],
            onClick = onVote
        )

    }
}

@Composable
fun VoteRankingCard(
    modifier: Modifier = Modifier,
    voteOption: Question,
    onClick: (List<PollResult>) -> Unit,
) {
    data class VariantItem(val origIndex: Int, val text: String)

    val items = remember {
        mutableStateListOf<VariantItem>().apply {
            addAll(voteOption.variants.mapIndexed { idx, s -> VariantItem(idx, s) })
        }
    }

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    val itemHeightDp = 64.dp
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeightDp.toPx() }

    Column {
        Column(
            Modifier
                .padding(24.dp)
                .then(modifier)
        ) {
            Text(
                voteOption.title,
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(R.string.ranking_based_tip),
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items, key = { _, v -> v.origIndex }) { index, item ->
                    val isDragging = draggingIndex == index
                    val translation = if (isDragging) dragOffsetY else 0f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .graphicsLayer {
                                translationY = translation
                                scaleX = if (isDragging) 1f else 0.95f
                                scaleY = if (isDragging) 1f else 0.95f
                            }
                            .background(
                                RarimeTheme.colors.componentPrimary,
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, RarimeTheme.colors.textPrimary, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { draggingIndex = index },
                                    onDrag = { change: PointerInputChange, dragAmount ->

                                        change.consume()

                                        dragOffsetY += dragAmount.y

                                        val from = draggingIndex ?: return@detectDragGestures


                                        val offsetPositions = (dragOffsetY / itemHeightPx).toInt()

                                        if (offsetPositions != 0) {
                                            val to = (from + offsetPositions).coerceIn(
                                                0,
                                                items.lastIndex
                                            )
                                            if (to != from) {
                                                val moved = items.removeAt(from)
                                                items.add(to, moved)
                                                draggingIndex = to
                                                dragOffsetY = 0f
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        draggingIndex = null
                                        dragOffsetY = 0f
                                    },
                                    onDragCancel = {
                                        draggingIndex = null
                                        dragOffsetY = 0f
                                    }
                                )
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource( R.drawable.ic_expand_vertical_line),
                                contentDescription = "",
                                tint = RarimeTheme.colors.textPrimary
                            )

                            VerticalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .height(24.dp)
                            )

                            Text(
                                item.text,
                                color = RarimeTheme.colors.textPrimary,
                                style = RarimeTheme.typography.buttonMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Next",
                onClick = {
                    val rankingOrder: MutableList<PollResult> = mutableListOf()
                    for(i in 0 .. voteOption.variants.size ){
                       val item = PollResult(
                           questionIndex = i,
                           answerIndex = items.get(i).origIndex,
                       )
                        rankingOrder.add(item)
                    }
                    onClick.invoke(rankingOrder.toList())
                },
                size = ButtonSize.Large,
                rightIcon = R.drawable.ic_arrow_right,
            )
        }
    }
}

@Preview
@Composable
private fun VoteRankingBasedScreenPreview() {
    Surface {
        VoteRankingBasedScreen(
            selectedPoll = MOCKED_RANKING_BASED_VOTE_ITEM,
            onBackClick = {},
            onVote = {})
    }
}
