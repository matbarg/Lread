package com.example.lread.ui.screens.reader

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.lread.data.model.Book
import com.example.lread.data.model.TextFont
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.TextSpacing
import com.example.lread.data.model.TextTheme
import com.example.lread.ui.navigation.NavRoute
import com.example.lread.ui.theme.LReadTheme
import com.example.lread.utils.ReaderJsBridge
import com.example.lread.utils.ReaderWebViewClient

@Composable
fun ReaderScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    val lifeCycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifeCycleOwner) {
        lifeCycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    // todo: move colors to viewmodel
    val color1 = Color(0xFF7BA3FF)
    val color2 = Color(0xFF0E5CFD)

    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {

            // rendering the webview needs to wait until the BookProgress was fetched from the db
            if (!viewModel.loadingInProgress.value) {
                AndroidView(
                    modifier = modifier.fillMaxSize(),
                    factory = {
                        WebView(it).apply {
                            webViewClient =
                                ReaderWebViewClient(getJsStyles = { uiState.value.currentStylesScript },
                                    getCurrentAnchorId = { uiState.value.currentAnchorId })
                            settings.apply {
                                javaScriptEnabled = true // js is needed to apply styles based on user events
                                //setSupportZoom(false) // todo: this setting has weird behaviour; sometimes it works, sometimes not

                                setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                                    /**
                                     * Logic to toggle the top bar:
                                     * becomes invisible when scrolling down and visible again when scrolling upwards
                                     */
                                    if (scrollY > oldScrollY) {
                                        viewModel.setTopBarVisible(false)
                                    } else {
                                        viewModel.setTopBarVisible(true)
                                    }

                                    /**
                                     * Logic to toggle the next chapter button when the user has fully scrolled down:
                                     * The content height needs to be adjusted with the scale, this makes the value comparable to scrollY
                                     * To find out the max scroll the (visible) height of the web view is subtracted,
                                     * the additional - 20 acts as a buffer for floating imprecisions and to make it a bit smoother
                                     */
                                    val webView = view as WebView // casts the view to a webview
                                    val adjustedContentHeight =
                                        (webView.contentHeight * webView.scale).toInt()
                                    val maxScroll = adjustedContentHeight - webView.height - 20

                                    if (scrollY > oldScrollY && scrollY >= maxScroll) {
                                        viewModel.setNextButtonVisible(true)
                                    } else {
                                        viewModel.setNextButtonVisible(false)
                                    }
                                }
                            }

                            // enables the callback to set the current anchorId from inside the webView
                            addJavascriptInterface(ReaderJsBridge { anchorId ->
                                viewModel.setCurrentAnchorId(anchorId)
                            }, "ReaderBridge")

                            loadUrl(uiState.value.currentChapterURL)
                        }
                    },
                    update = { webView ->
                        if (webView.url != uiState.value.currentChapterURL) { // prevents the webView from reloading if only the css was changed
                            webView.loadUrl(uiState.value.currentChapterURL)
                        } else {
                            webView.evaluateJavascript(uiState.value.currentStylesScript, null)
                        }
                    }
                )
            } else {
                CircularProgressIndicator(modifier = modifier.align(Alignment.Center))
            }

            /**
             * Top bar:
             * (It's not the Scaffolds topBar directly because it acts more as an overlay)
             */
            val topBackgroundColor = animateColorAsState(
                targetValue = if (uiState.value.topBarVisible) color1 else Color(0x007BA3FF),
                animationSpec = tween(durationMillis = 300),
            )

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(topBackgroundColor.value)
                    .padding(15.dp),
            ) {
                // Back button
                IconButton(
                    modifier = modifier
                        .align(Alignment.CenterStart)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(color2),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }

                // Center text with title and author
                AnimatedVisibility(
                    modifier = modifier.align(Alignment.Center),
                    visible = uiState.value.topBarVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 300))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.value.book.title, fontWeight = FontWeight.Bold)
                        Text(text = uiState.value.book.author)
                    }
                }

                // Toggle settings button
                IconButton(
                    modifier = modifier
                        .align(Alignment.CenterEnd)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(color2),
                    onClick = {
                        viewModel.toggleSettings()
                        viewModel.setTopBarVisible(true)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Settings"
                    )
                }
            }

            /**
             * Settings panel
             */
            AnimatedVisibility(
                visible = uiState.value.settingsVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                Column(
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 90.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(color1)
                        .padding(20.dp)
                ) {
                    Text("Select Chapter")

                    ChapterButtonRow(
                        currentChapter = uiState.value.currentChapter,
                        totalChapters = uiState.value.book.chapters.size
                    ) {
                        viewModel.setCurrentChapter(it)
                    }

                    Spacer(modifier = modifier.height(12.dp))

                    Text("Text Display Settings")

                    TextSettingDropdown(label = uiState.value.textSize.label, items = TextSize.entries) { viewModel.setTextSize(it) }
                    TextSettingDropdown(label = uiState.value.textSpacing.label, items = TextSpacing.entries) { viewModel.setTextSpacing(it) }
                    TextSettingDropdown(label = uiState.value.textTheme.label, items = TextTheme.entries) { viewModel.setTextTheme(it) }
                    TextSettingDropdown(label = uiState.value.textFont.label, items = TextFont.entries) { viewModel.setTextFont(it) }
                }
            }

            /**
             * Next chapter button
             */
            AnimatedVisibility(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                visible = uiState.value.nextButtonVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)) + slideInVertically(animationSpec = tween(durationMillis = 500), initialOffsetY = { it / 2 }),
                exit = fadeOut(animationSpec = tween(durationMillis = 100)) + slideOutVertically(animationSpec = tween(durationMillis = 100), targetOffsetY = { it / 4 })
            ) {
                Button(
                    modifier = modifier.shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
                    colors = ButtonColors(
                        containerColor = color2,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Green,
                        disabledContentColor = Color.Yellow
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        if (uiState.value.onLastChapter) {
                            viewModel.closeBook()
                            navController.popBackStack()
                        } else {
                            viewModel.goToNextChapter()
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val msg = if (uiState.value.onLastChapter) "Close book" else "Next chapter"

                        Text(msg)
                        Icon(
                            imageVector = if (uiState.value.onLastChapter) Icons.Default.Close else Icons.Default.KeyboardArrowRight,
                            contentDescription = msg
                        )
                    }
                }
            }
        }
    }
}