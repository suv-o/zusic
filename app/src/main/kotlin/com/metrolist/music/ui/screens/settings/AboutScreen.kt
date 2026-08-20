/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.metrolist.music.BuildConfig
import com.metrolist.music.LocalPlayerAwareWindowInsets
import com.metrolist.music.R
import com.metrolist.music.ui.component.IconButton
import com.metrolist.music.ui.utils.backToMain

// Imports for theme
import com.metrolist.music.constants.DarkModeKey
import com.metrolist.music.ui.screens.settings.DarkMode
import com.metrolist.music.utils.rememberEnumPreference

private data class Contributor(
    val name: String,
    val role: String,
    val githubHandle: String,
    val avatarUrl: String = "https://github.com/$githubHandle.png",
    val githubUrl: String = "https://github.com/$githubHandle",
    val polygon: RoundedPolygon? = null
)

private data class CommunityLink(
    val label: String,
    val iconRes: Int,
    val url: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val leadDeveloper = Contributor(
    name = "Subhajit Adhikary",
    role = "Developer",
    githubHandle = "suv-o",
    polygon = MaterialShapes.Cookie9Sided
)

private val socialLinks = listOf(
    CommunityLink("GitHub", R.drawable.github, "https://github.com/suv-o"),
    CommunityLink("Instagram", R.drawable.instagram, "https://www.instagram.com/suv.jt")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current
    val windowInsets = LocalPlayerAwareWindowInsets.current
    
    val isSystemDark = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.ON)
    val isDarkTheme = remember(darkTheme, isSystemDark) {
        if (darkTheme == DarkMode.AUTO) isSystemDark else darkTheme == DarkMode.ON
    }
    
    val appIconBgColor = if (isDarkTheme) Color.White else Color.Black
    val appIconTintColor = if (isDarkTheme) Color.Black else Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()) // Appbar padding
                // Mini-player scroll issue fix exactly like original code
                .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)) 
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            // 1. App Header Card with new text
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = appIconBgColor,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.app_icon),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                colorFilter = ColorFilter.tint(appIconTintColor)
                            )
                        }
                    }

                    Spacer(Modifier.width(20.dp))

                    Column {
                        // Resized text slightly to fit 3 lines perfectly
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium, 
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = BuildConfig.BUILD_TYPE.lowercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = BuildConfig.VERSION_NAME,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        
                        // New added line
                        Text(
                            text = "by suv-o & zoo™",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // 2. Developer Image with Theme-aware App Icon fallback
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(leadDeveloper.polygon?.toShape() ?: CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Background Fallback Icon (Inverts with theme)
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    colorFilter = ColorFilter.tint(appIconTintColor)
                )
                // Actual Image loading on top
                AsyncImage(
                    model = leadDeveloper.avatarUrl,
                    contentDescription = leadDeveloper.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(32.dp))

            // 3. Developer Card with GitHub button fixed alignment
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp) // Space for overlapping button
                ) {
                    Column(
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 24.dp, end = 64.dp)
                    ) {
                        Text(
                            text = leadDeveloper.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Text(
                            text = leadDeveloper.role,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Top Right GitHub Button moved perfectly inwards
                Surface(
                    onClick = { uriHandler.openUri(leadDeveloper.githubUrl) },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = (-4).dp) // Moved inward to align with card boundaries
                        .size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.github),
                            contentDescription = "GitHub Repository",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 4. Social Links Card
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    socialLinks.forEach { link ->
                        Surface(
                            onClick = { uriHandler.openUri(link.url) },
                            color = Color.Transparent,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(link.iconRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                                Spacer(Modifier.width(20.dp))
                                Text(
                                    text = link.label,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}
