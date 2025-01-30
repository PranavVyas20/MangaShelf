package com.example.mangashelf.ui.manga_listing

import android.graphics.Paint.Align
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mangashelf.R
import com.example.mangashelf.domain.model.GroupedMangas
import com.example.mangashelf.domain.model.MangaDto
import com.example.mangashelf.domain.model.MangaSortingOrder
import com.example.mangashelf.ui.components.debounceClickable
import com.example.mangashelf.ui.manga_listing_error.MangaListErrorScreen
import com.example.mangashelf.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MangaListScreen(
    uiState: MangaListViewModel.MangaListUiState,
    onLoadMoreData: () -> Unit,
    onRetryClicked: () -> Unit,
    onSortByClicked: (currentSortOrder: MangaSortingOrder) -> Unit,
    onMangaItemClicked: (mangaId: String) -> Unit,
    removeToastMessage: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    uiState.toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        removeToastMessage()

    }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_121212))
            .systemBarsPadding()
            .padding(top = 8.dp)
    ) {
        if (uiState.errorMessage != null) {
            MangaListErrorScreen(onRetryClicked = {
                onRetryClicked()
            })
        } else if (!uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "ALL BOOKS",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = Typography.headlineMedium.copy(color = colorResource(R.color.color_f8f8f8))
                )
                AnimatedVisibility(visible = uiState.sortingOrder == MangaSortingOrder.PUBLISHED_YEAR) {
                    PublishedFiltersBar(
                        mangasPublishedYears = uiState.mangasPublishedYears,
                        allMangas = uiState.allFetchedMangas,
                        groupedMangas = uiState.groupedMangas,
                        lazyListState = lazyListState,
                        onTabClicked = { index, year ->
                            scope.launch(Dispatchers.Default) {
//                                val totalItemsToScroll = uiState.groupedMangas
//                                    .take(index + 1)
//                                    .sumOf { it.mangas.size }

                                val indexToScroll = uiState.groupedMangas
                                    .take(index + 1)
                                    .sumOf { it.mangas.size } - uiState.groupedMangas[index].mangas.size
                                withContext(Dispatchers.Main) {
//                                    Log.d("index_tagg", "$totalItemsToScroll")
//                                    lazyListState.animateScrollToItem(indexToScroll)
                                    lazyListState.scrollToItem(indexToScroll)
                                }
                            }
                        }
                    )
                }
                if (uiState.showLoadingForSorting) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    SortingFilter(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        totalBooks = uiState.totalMangas,
                        onFilterClick = {
                            onSortByClicked(it)
                        }
                    )
                    MangaList(
                        listState = lazyListState,
                        allMangas = uiState.allFetchedMangas,
                        onLoadMoreData = onLoadMoreData,
                        onMangaClick = {
                            onMangaItemClicked(it)
                        }
                    )
                }

            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }


    }
}

@Composable
fun SortingFilter(
    modifier: Modifier,
    totalBooks: Int,
    onFilterClick: (currentSortingOrder: MangaSortingOrder) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$totalBooks books", style = Typography.titleSmall.copy(fontSize = 16.sp))
        Spacer(Modifier.weight(1f))
        Box {
            DropdownMenu(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(color = colorResource(R.color.color_252525)),
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }) {
                MangaSortingOrder.entries.forEach {
                    DropdownMenuItem(text =
                    { Text(text = it.key, style = Typography.titleSmall) },
                        onClick = {
                            onFilterClick(it)
                            isExpanded = false
                        })
                }
            }
        }

        Icon(
            modifier = Modifier.clickable {
                isExpanded = !isExpanded
            },
            imageVector = Icons.Default.FilterList,
            tint = colorResource(R.color.color_c1c1c1),
            contentDescription = "filter_icon"
        )
    }
}

@Composable
fun MangaList(
    allMangas: List<MangaDto>,
    listState: LazyListState,
    onMangaClick: (id: String) -> Unit,
    onLoadMoreData: () -> Unit
) {
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) onLoadMoreData()
    }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(allMangas, key = {it.id}) { manga ->
            MangaListItem(manga = manga) { id ->
                Log.d("id_tagg", ": $id")
                onMangaClick(id)
            }
        }
    }
}

@Composable
fun PublishedFiltersBar(
    mangasPublishedYears: List<Int>,
    allMangas: List<MangaDto>,
    groupedMangas: List<GroupedMangas>,
    lazyListState: LazyListState,
    onTabClicked: (index: Int, year: Int) -> Unit
) {
    var selectedFilterTabIndex by remember { mutableIntStateOf(0) }
    var currentYear by remember { mutableStateOf<Int?>(null) }
    val currentGroupedMangas by rememberUpdatedState(groupedMangas)
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { index ->
                val newYear = allMangas.getOrNull(index)?.publishedYear
                if (newYear != null && newYear != currentYear) {
                    currentYear = newYear
                    selectedFilterTabIndex =
                        currentGroupedMangas.indexOfFirst { it.publishedYear == newYear }
                }
            }
    }
    ScrollableTabRow(
        divider = {},
        selectedTabIndex = selectedFilterTabIndex,
        containerColor = Color.Transparent,
        edgePadding = 20.dp,
        indicator = {}
    ) {
        mangasPublishedYears.forEachIndexed { index, manga ->
            PublishedYearFilterItem(
                year = manga,
                isSelected = index == selectedFilterTabIndex,
                onClick = {
                    selectedFilterTabIndex = index
                    onTabClicked(index, manga)
                },
                shouldProvideEndPadding = (index != mangasPublishedYears.lastIndex)
            )
        }
    }
}

@Composable
fun PublishedYearFilterItem(
    year: Int,
    isSelected: Boolean,
    shouldProvideEndPadding: Boolean,
    onClick: () -> Unit
) {
    val (textColor, bgColor) =
        if (isSelected) {
            colorResource(R.color.color_121212) to colorResource(R.color.color_f9cb55)
        } else {
            colorResource(R.color.color_f8f8f8) to colorResource(R.color.color_252525)
        }

    Text(
        text = year.toString(),
        style = Typography.headlineSmall.copy(
            color = textColor,
            fontWeight = FontWeight.Light
        ),
        modifier = Modifier
            .then(if (shouldProvideEndPadding) Modifier.padding(end = 8.dp) else Modifier)
            .background(color = bgColor, shape = RoundedCornerShape(6.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp)
            .clickable { onClick() }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun MangaListItem(manga: MangaDto, onClick: (mangaId: String) -> Unit) {
    val isPreviewMode = LocalInspectionMode.current
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .debounceClickable {
                onClick(manga.id)
            }
            .height(IntrinsicSize.Max)
    ) {
        if (isPreviewMode) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(height = 140.dp, width = 120.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        } else {
            GlideImage(
                model = manga.imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(height = 140.dp, width = 100.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = manga.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = Typography.titleMedium.copy(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier.clickable {
                        Toast.makeText(
                            context,
                            "Go to manga details to bookmark!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    imageVector = if (manga.isFavourite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    tint = colorResource(R.color.color_f8f8f8),
                    contentDescription = "icon"
                )
            }

            Text(text = "Yoto Suzuki", style = Typography.titleSmall)
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
                Text(
                    text = manga.score.toString(),
                    style = Typography.titleSmall.copy(fontSize = 16.sp)
                )
            }
            Text(text = manga.category, style = Typography.titleSmall)
            Text(text = manga.publishedYear.toString(), style = Typography.titleSmall)
        }

    }
}


