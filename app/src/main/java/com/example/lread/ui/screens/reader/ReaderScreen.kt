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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lread.data.model.TextFont
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.TextSpacing
import com.example.lread.data.model.TextTheme
import com.example.lread.ui.theme.lreadBlue
import com.example.lread.ui.theme.lreadLightBlue
import com.example.lread.ui.theme.lreadLightBlueClear
import com.example.lread.utils.ReaderJsBridge
import com.example.lread.utils.ReaderWebViewClient

@Composable
fun ReaderScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifeCycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifeCycleOwner) {
        lifeCycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (!viewModel.loadingInProgress.value) {
                AndroidView(
                    modifier = modifier.fillMaxSize(),
                    factory = {
                        WebView(it).apply {
                            webViewClient =
                                ReaderWebViewClient(
                                    getJsStyles = { uiState.currentStylesScript },
                                    getCurrentAnchorId = { uiState.currentAnchorId }
                                )
                            settings.apply {
                                javaScriptEnabled = true
                                setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                                    if (scrollY > oldScrollY && !uiState.settingsVisible) {
                                        viewModel.setTopBarVisible(false)
                                    } else {
                                        viewModel.setTopBarVisible(true)
                                    }

                                    val webView = view as WebView
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

                            addJavascriptInterface(ReaderJsBridge { anchorId ->
                                viewModel.setCurrentAnchorId(anchorId)
                            }, "ReaderBridge")

                            loadUrl(uiState.currentChapterURL)
                        }
                    },
                    update = { webView ->
                        if (webView.url != uiState.currentChapterURL) {
                            webView.loadUrl(uiState.currentChapterURL)
                        } else {
                            // Only evaluate JS styles if the URL hasn't changed to avoid re-applying on new chapter load
                            // The webViewClient.onPageFinished handles initial styles and fonts.
                            // This update block ensures style changes are applied without reloading the whole page.
                            webView.evaluateJavascript(uiState.currentStylesScript, null)
                        }
                    }
                )
            } else {
                CircularProgressIndicator(modifier = modifier.align(Alignment.Center))
            }

            val topBackgroundColor by animateColorAsState(
                targetValue = if (uiState.topBarVisible) lreadLightBlue else lreadLightBlueClear,
                animationSpec = tween(durationMillis = 300),
                label = "topBackgroundColorAnimation"
            )

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(topBackgroundColor)
                    .padding(15.dp),
            ) {
                IconButton(
                    modifier = modifier
                        .align(Alignment.CenterStart)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(lreadBlue),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                AnimatedVisibility(
                    modifier = modifier.align(Alignment.Center),
                    visible = uiState.topBarVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 300))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.book.title, fontWeight = FontWeight.Bold)
                        Text(text = uiState.book.author)
                    }
                }

                IconButton(
                    modifier = modifier
                        .align(Alignment.CenterEnd)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(lreadBlue),
                    onClick = {
                        viewModel.toggleSettings()
                        viewModel.setTopBarVisible(true) // Ensure top bar is visible when settings toggle
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.settingsVisible) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Toggle Settings",
                        tint = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState.settingsVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                var dropdownExpanded by remember { mutableStateOf(false) } // State for the music dropdown
                Column(
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 90.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(lreadLightBlue)
                        .padding(20.dp)
                ) {
                    Text("Select Chapter")

                    ChapterButtonRow(
                        currentChapter = uiState.currentChapter,
                        totalChapters = uiState.book.chapters.size
                    ) {
                        viewModel.setCurrentChapter(it)
                    }

                    Spacer(modifier = modifier.height(12.dp))

                    Text("Text Display Settings")

                    TextSettingDropdown(
                        buttonText = "Text size",
                        currentValueText = uiState.textSize.label,
                        items = TextSize.entries
                    ) { viewModel.setTextSize(it) }

                    TextSettingDropdown(
                        buttonText = "Text spacing",
                        currentValueText = uiState.textSpacing.label,
                        items = TextSpacing.entries
                    ) { viewModel.setTextSpacing(it) }

                    TextSettingDropdown(
                        buttonText = "Text theme",
                        currentValueText = uiState.textTheme.label,
                        items = TextTheme.entries
                    ) { viewModel.setTextTheme(it) }

                    TextSettingDropdown(
                        buttonText = "Text font",
                        currentValueText = uiState.textFont.label,
                        items = TextFont.entries
                    ) { viewModel.setTextFont(it) }

                    Spacer(Modifier.height(12.dp))
                    Text("Background Music")

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {
                            viewModel.setMusicEnabled(!uiState.isMusicEnabled)
                        }) {
                            Text(if (uiState.isMusicEnabled) "Pause Music" else "Play Music")
                        }

                        // Button to open song selection dropdown
                        Button(onClick = { dropdownExpanded = true }) {
                            Text(uiState.selectedTrack) // Show currently selected track name
                        }

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            // IMPORTANT: These display names must match the 'when' conditions in ReaderViewModel
                            listOf("Lo-fi Vibes", "Rainy Calm", "Reading Flow").forEach { name ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        viewModel.setSelectedTrack(name)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Next Chapter Button
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                visible = uiState.nextButtonVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 },
                exit = fadeOut(tween(100)) + slideOutVertically(tween(100)) { it / 4 }
            ) {
                Button(
                    modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lreadBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        if (uiState.onLastChapter) {
                            viewModel.closeBook()
                            navController.popBackStack()
                        } else {
                            viewModel.goToNextChapter()
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val msg = if (uiState.onLastChapter) "Close Book" else "Next Chapter"
                        Text(msg)
                        Icon(
                            imageVector = if (uiState.onLastChapter)
                                Icons.Default.Close else Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}