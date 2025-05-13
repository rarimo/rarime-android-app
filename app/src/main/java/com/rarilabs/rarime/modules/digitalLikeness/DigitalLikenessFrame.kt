import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun DigitalLikenessFrame(
    faceImage: ImageBitmap,
    @DrawableRes frameRes: Int,
    modifier: Modifier = Modifier,
    frameSize: Dp = 320.dp,       // size of your white PNG background
    faceSize: Dp = 270.dp,        // make this larger than frameSize!
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(frameSize),
        contentAlignment = Alignment.TopCenter
    ) {
        // 1) background frame (bottom)
        Image(
            painter = painterResource(frameRes),
            contentDescription = null,
            modifier = Modifier
                .size(frameSize)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        // 2) face image
        Image(
            bitmap = faceImage,
            contentDescription = null,
            alignment = BiasAlignment(
                horizontalBias = 0f,
                verticalBias = 0.1f
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(faceSize)
                .clip(LeafShape())
        )
    }
}


// Draw shape for face photo
class LeafShape(
    private val bigRadius: Dp = 100.dp,
    private val smallRadius: Dp = 25.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val bigR = with(density) { bigRadius.toPx() }
        val smallR = with(density) { smallRadius.toPx() }

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, 0f, size.width, size.height),
                    topLeft = CornerRadius(bigR, bigR),
                    topRight = CornerRadius(smallR, smallR),
                    bottomRight = CornerRadius(bigR, bigR),
                    bottomLeft = CornerRadius(smallR, smallR)
                )
            )
        }
        return Outline.Generic(path)
    }
}