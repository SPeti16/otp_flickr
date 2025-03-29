package com.test.otp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.test.otp.R
import com.test.otp.navigation.ScreenDetail
import com.test.otp.network.NetworkCategory
import com.test.otp.ui.viewmodels.MainViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainScreen(
    navController: NavHostController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MainViewModel = hiltViewModel()
){
    val isConnected by viewModel.isConnected.collectAsState()

    val allPhoto by viewModel.allPhoto.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    var page by remember { mutableIntStateOf(1) }

    val gridState = rememberLazyGridState()

    val search by viewModel.search.collectAsState()

    val searchTextFieldState by remember { mutableStateOf(TextFieldState(search)) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.layoutInfo.visibleItemsInfo }
            .collect { (_, visibleItems) ->
                val totalItems = gridState.layoutInfo.totalItemsCount
                val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: return@collect

                if (lastVisibleIndex == totalItems - 1) {
                    if(viewModel.total>=20*page){
                        page++
                        viewModel.search(page)
                    }
                }
            }
    }

    when(isConnected){
        NetworkCategory.LOADING -> CircularProgressIndicator(
            modifier = Modifier.wrapContentSize(),
            color = MaterialTheme.colorScheme.secondary
        )
        NetworkCategory.NONE -> Card(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .padding(24.dp)
        ) {
            Column (
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(R.string.main_internet_error))
                Row (
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(onClick = { viewModel.startDataDownload() }){
                        Text(text = stringResource(R.string.main_again_button))
                    }
                }
            }
        }
        else -> {
            Column {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){

                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null
                    )
                    BasicTextField(
                        state = searchTextFieldState,
                        modifier = Modifier
                            .weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        onKeyboardAction = {
                            page = newSearch(
                                viewModel,
                                searchTextFieldState,
                                keyboardController,
                                focusManager
                            )
                        }
                    )

                    AnimatedVisibility(searchTextFieldState.text.isNotBlank() && searchTextFieldState.text != search) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = null,
                            tint = Color.Blue,
                            modifier = Modifier.clickable {
                                page = newSearch(
                                    viewModel,
                                    searchTextFieldState,
                                    keyboardController,
                                    focusManager
                                )
                            }
                        )
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    allPhoto.isNotEmpty(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        state = gridState
                    ) {
                        items(allPhoto.size) { i ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray)
                                    .clickable {
                                        navController.navigate(ScreenDetail(allPhoto[i].id, searchTextFieldState.text.toString()))
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(allPhoto[i].getImageUrl())
                                        .build(),
                                    contentDescription = allPhoto[i].title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                        .sharedElement(
                                            state = rememberSharedContentState(key = stringResource(R.string.state_key, allPhoto[i].id)),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                            boundsTransform = { _, _ ->
                                                tween(durationMillis = 300)
                                            }
                                        )
                                )
                            }
                        }
                        if (isLoading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

private fun newSearch(
    viewModel: MainViewModel,
    searchTextFieldState: TextFieldState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) : Int{
    viewModel.changeSearch(searchTextFieldState.text.toString())
    keyboardController?.hide()
    focusManager.clearFocus()
    return 1
}
