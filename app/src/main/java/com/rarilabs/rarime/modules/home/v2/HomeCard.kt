package com.rarilabs.rarime.modules.home.v2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme


data class CardProperties(
    val header: String,
    val subTitle: String,
    val icon: Int,
    val image: Int,
    val backgroundGradient: Brush,
)


@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    cardProperties: CardProperties,
    footer: @Composable () -> Unit,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onCardClick,
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            Modifier
                .background(cardProperties.backgroundGradient)
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, end = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        modifier = Modifier.padding(),
                        style = RarimeTheme.typography.h5,
                        text = cardProperties.header
                    )
                    Text(
                        modifier = Modifier.padding(),
                        style = RarimeTheme.typography.h4,
                        text = cardProperties.subTitle,

                        )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(72.dp)
                        .height(72.dp)
                        .background(RarimeTheme.colors.componentPrimary, CircleShape)
                ) {
                    AppIcon(
                        id = cardProperties.icon,
                        size = 32.dp,
                        tint = RarimeTheme.colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(cardProperties.image),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.weight(1f))

            footer()
        }
    }
}


@Preview
@Composable
private fun HomeCardPreview() {

    val prop = CardContent(
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
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)) {
                TransparentButton(enabled = true,
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    leftIcon = R.drawable.ic_arrow_left,
                    text = "Enabled",
                    onClick = { })
            }
        }
    )

    HomeCard(cardProperties = prop.properties, onCardClick = {}, footer = {
//        Column(
//            modifier = Modifier
//                .padding(24.dp)
//        ) {
//            Row(
//                Modifier
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(RarimeTheme.colors.baseWhite)
//                    .padding(vertical = 8.dp, horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "149250-1596", style = RarimeTheme.typography.h5)
//                VerticalDivider(
//                    modifier = Modifier
//                        .height(24.dp)
//                        .padding(horizontal = 16.dp)
//                )
//                AppIcon(id = R.drawable.ic_copy_simple)
//            }
//            Spacer(Modifier.height(20.dp))
//            Text(text = "*Nothing leaves thi devise")
//        }

        prop.footer()
    })
}