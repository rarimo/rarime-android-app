import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun WinningFaceCard(
    modifier: Modifier = Modifier,
    imageSrc: String,
    @DrawableRes placeholderRes: Int,
    name: String,
    description: String,
    winnerAddress: String,
    prizeAmount: Float,
    prizeSymbol: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(16.dp),
            ambientColor = Color(0xFF9D4EDD).copy(alpha = 1f),
            spotColor = Color(0xFF9D4EDD).copy(alpha = 1f)
        )
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = RarimeTheme.colors.backgroundPrimary,
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.winner_face_card_title),
                    style = RarimeTheme.typography.subtitle6.copy(RarimeTheme.colors.textPrimary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = imageSrc)
                                .apply(block = fun ImageRequest.Builder.() {
                                    placeholder(placeholderRes)
                                }).build()
                        ),
                        contentDescription = stringResource(R.string.winner_face_card_image_description),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            style = RarimeTheme.typography.h5.copy(RarimeTheme.colors.textPrimary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = RarimeTheme.typography.body5,
                            color = RarimeTheme.colors.textSecondary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = RarimeTheme.colors.componentPrimary.copy(0.05f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = stringResource(R.string.winner_face_card_winner_lbl),
                            style = RarimeTheme.typography.subtitle6.copy(RarimeTheme.colors.textPrimary)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = winnerAddress,
                            style = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary),
                            textDecoration = TextDecoration.Underline
                        )
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = stringResource(R.string.winner_face_card_prize_lbl),
                            style = RarimeTheme.typography.subtitle6.copy(RarimeTheme.colors.textPrimary)
                        )
                        Spacer(Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = prizeAmount.toString().format(),
                                style = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary),
                            )
                            prizeSymbol()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun WinningFaceCardPreview_LightMode() {
    AppTheme {
        Column(
            modifier = Modifier
                .height(300.dp)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            WinningFaceCard(
                imageSrc = "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg",
                placeholderRes = R.drawable.drawable_digital_likeness,
                name = "Vitalik Buterin",
                description = "Ethereum co-founder",
                winnerAddress = "0x00000...0000",
                prizeAmount = 0.3F,
                prizeSymbol = {
                    Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
                },
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WinningFaceCardPreview_DarkMode() {
    AppTheme {
        Column(
            modifier = Modifier
                .height(300.dp)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            WinningFaceCard(
                imageSrc = "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg",
                placeholderRes = R.drawable.drawable_digital_likeness,
                name = "Vitalik Buterin",
                description = "Ethereum co-founder",
                winnerAddress = "0x00000...0000",
                prizeAmount = 0.3F,
                prizeSymbol = {
                    Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
                },
            )
        }
    }
}