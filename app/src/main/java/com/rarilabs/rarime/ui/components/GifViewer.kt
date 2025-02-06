package com.rarilabs.rarime.ui.components

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun GifViewer(
    modifier: Modifier = Modifier,
    gifId: Int,
) {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = gifId).apply(block = { size(Size.ORIGINAL) })
                .build(),
            imageLoader = imageLoader,
            contentScale = ContentScale.FillBounds,
        ),
        contentDescription = null
    )
}

//@Preview(showBackground = true)
//@Composable
//private fun GifViewerPreview() {
//    GifViewer(gifId = R.raw.read_nfc_external)
//}