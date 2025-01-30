package com.example.mangashelf.ui.manga_listing_error

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.example.mangashelf.R
import com.example.mangashelf.ui.theme.Typography

@Composable
fun MangaListErrorScreen(onRetryClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_121212)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(R.drawable.manga_error), contentDescription = "error_image")
        Button(onClick = onRetryClicked) {
            Text(text = "Retry", style = Typography.titleMedium)
        }
    }
}
