package com.studio.tline.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studio.tline.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector,
    val color: Color
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(R.string.onboarding_title_1, R.string.onboarding_desc_1, Icons.Default.Camera, Color.Cyan),
        OnboardingPage(R.string.onboarding_title_2, R.string.onboarding_desc_2, Icons.Default.Layers, Color.White),
        OnboardingPage(R.string.onboarding_title_3, R.string.onboarding_desc_3, Icons.Default.AutoAwesome, Color.Cyan),
        OnboardingPage(R.string.onboarding_title_4, R.string.onboarding_desc_4, Icons.Default.DoneAll, Color.Green)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(32.dp).navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicadores
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == index) Color.Cyan else Color.Gray)
                        )
                    }
                }

                // Botão
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) 
                            stringResource(R.string.onboarding_button_start) else 
                            stringResource(R.string.onboarding_button_next),
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(160.dp),
                    shape = CircleShape,
                    color = page.color.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(40.dp).size(80.dp),
                        tint = page.color
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Text(
                    text = stringResource(page.titleRes),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(page.descRes),
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
