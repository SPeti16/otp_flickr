package com.test.otp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.test.otp.R
import com.test.otp.navigation.ScreenDetail
import com.test.otp.ui.viewmodels.DetailViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    args: ScreenDetail,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: DetailViewModel = hiltViewModel()
){
    LaunchedEffect(args.id) {
        viewModel.startDownloadInfo(args.id)
    }

    val info by viewModel.photoInfo.collectAsState()

    var fullScreen by remember { mutableStateOf(false) }

    val imageWeight by animateFloatAsState(
        targetValue = if (fullScreen) 1f else 0.5f,
        label = ""
    )
    val textWeight = (1f - imageWeight).coerceAtLeast(0.0001f)

    val scale = remember { mutableFloatStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale.value *= zoomChange
        offset.value += panChange
    }

    val zoomModifier = if(fullScreen){
        Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale.floatValue,
                scaleY = scale.floatValue,
                translationX = offset.value.x,
                translationY = offset.value.y
            )
            .transformable(state = state)
    }
    else{
        Modifier.fillMaxSize().clip(
            RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
        )
    }

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        AnimatedVisibility(info == null) {
            CircularProgressIndicator()
        }
        AnimatedVisibility(info != null,
            modifier = Modifier.fillMaxWidth().weight(imageWeight).animateContentSize()) {
            Box(
                modifier = zoomModifier
                    .clickable {
                        if(fullScreen)
                        {
                            scale.floatValue = 1f
                            offset.value = Offset.Zero
                        }
                        else{
                            scale.floatValue = 3f
                        }
                        fullScreen = !fullScreen
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(info?.getImageUrl())
                        .build(),
                    contentDescription = null,
                    contentScale = if(fullScreen) ContentScale.Fit else ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            state = rememberSharedContentState(
                                key = stringResource(
                                    R.string.state_key,
                                    args.id
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 300)
                            }
                        )
                )
            }
        }
        AnimatedVisibility((info != null && !fullScreen),
            modifier = Modifier.fillMaxWidth().weight(textWeight)) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
            ){
                Text(
                    text = info?.title?.content?:"",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility((info?.description?.content?:"") != "") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = info?.description?.content?:"",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.user, info?.owner?.username?:""))
                Text(text = stringResource(R.string.data, info?.dates?.taken?:""))

            }
        }
    }

}