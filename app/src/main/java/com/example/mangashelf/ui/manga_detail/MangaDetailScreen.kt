package com.example.mangashelf.ui.manga_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangashelf.R
import com.example.mangashelf.ui.theme.Typography

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MangaDetailScreen(
    uiState: MangaDetailUiState,
    onSaveToDbClick: (markAsFavorite: Boolean) -> Unit,
    navController: NavController
) {
    uiState.mangaDetails?.let {
        var isSaved by remember { mutableStateOf(uiState.mangaDetails.isFavourite) }
        var shouldFetchUpdatedDataFromDb by remember { mutableStateOf(false) }
        BackHandler {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "shouldFetchFromDb",
                shouldFetchUpdatedDataFromDb
            )
            navController.popBackStack()
        }
        val isPreviewMode = LocalInspectionMode.current
        Box(modifier = Modifier.fillMaxSize()) {
            GradientImage(uiState.mangaDetails.imageUrl)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            tint = colorResource(R.color.color_f8f8f8),
                            contentDescription = "back_icon"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        modifier = Modifier,
                        text = "CHAPTER ${uiState.chapterNameRandom}",
                        style = Typography.headlineMedium.copy(color = colorResource(R.color.color_f8f8f8))
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        shouldFetchUpdatedDataFromDb = true
                        isSaved = !isSaved
                        onSaveToDbClick(isSaved)
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            tint = colorResource(R.color.color_f8f8f8),
                            contentDescription = "icon"
                        )
                    }
                }

                if (isPreviewMode) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "image",
                        contentScale = ContentScale.Crop
                    )
                } else {
                    GlideImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 14.dp,
                                clip = true,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .border(
                                width = 1.dp,
                                color = colorResource(R.color.color_f8f8f8).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp)
                            .height(350.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        model = uiState.mangaDetails?.imageUrl,
                        contentDescription = ""
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = uiState.mangaDetails?.category.orEmpty(),
                        style = Typography.titleMedium.copy(color = colorResource(R.color.color_f8f8f8))
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            tint = colorResource(R.color.color_f8f8f8),
                            contentDescription = "score_icon"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        uiState.mangaDetails?.score?.toString()?.let {
                            Text(
                                text = it, style = Typography.titleSmall.copy(
                                    fontSize = 16.sp, color = colorResource(R.color.color_f8f8f8)
                                )
                            )
                        }
                    }
                }
                Text(
                    text = uiState.mangaDetails?.title.orEmpty(),
                    style = Typography.headlineLarge.copy(color = colorResource(R.color.color_f8f8f8))
                )
                Text(
                    text = "Released: ${uiState.mangaDetails?.publishedYear}",
                    style = Typography.titleMedium.copy(color = colorResource(R.color.color_f8f8f8))
                )

            }
        }
    }

}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GradientImage(imageUrl: String) {
    GlideImage(
        model = imageUrl, contentDescription = "", modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val gradient = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = size.height / 5,
                    endY = size.height
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(gradient, blendMode = BlendMode.SrcOver)
                }
            }, contentScale = ContentScale.Crop
    )
}