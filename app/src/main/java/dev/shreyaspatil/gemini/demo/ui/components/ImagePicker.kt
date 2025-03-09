package dev.shreyaspatil.gemini.demo.ui.components

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImagePicker(modifier: Modifier = Modifier, onImagePicked: (ImageBitmap) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val imageUri = uri ?: run {
                Toast.makeText(context, "Image not attached", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            scope.launch(Dispatchers.Default) {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmap?.let {
                    onImagePicked(it.asImageBitmap())
                }
            }
        }

    Icon(
        Icons.Filled.Image,
        contentDescription = "Attach Image",
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = {
                launcher.launch("image/*")
            }),
    )
}