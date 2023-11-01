package com.chirrio.filepicker

import androidx.compose.ui.graphics.ImageBitmap

class ImageWithData(
    val file: MPFile<Any>,
    val data: ByteArray,
    val imageBitmap: ImageBitmap
)