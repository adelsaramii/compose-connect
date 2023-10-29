package com.chirrio.filepicker

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

data class AndroidFile(
    override val path: String,
    override val platformFile: Uri,
) : MPFile<Uri>

@Composable
actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: List<String>,
    onFileSelected: FileSelected
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
        if (result != null) {
            onFileSelected(AndroidFile(result.toString(), result))
        } else {
            onFileSelected(null)
        }
    }

    val mimeTypeMap = MimeTypeMap.getSingleton()
    val mimeTypes = if (fileExtensions.isNotEmpty()) {
        fileExtensions.mapNotNull { ext ->
            mimeTypeMap.getMimeTypeFromExtension(ext)
        }.toTypedArray()
    } else {
        emptyArray()
    }

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(mimeTypes)
        }
    }
}

@Composable
actual fun DirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { result ->
        onFileSelected(result?.toString())
    }

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(null)
        }
    }
}