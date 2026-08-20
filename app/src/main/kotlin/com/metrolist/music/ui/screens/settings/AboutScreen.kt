/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.RoundedPolygon
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.metrolist.innertube.models.WatchEndpoint
import com.metrolist.music.BuildConfig
import com.metrolist.music.LocalPlayerAwareWindowInsets
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.playback.PlayerConnection
import com.metrolist.music.playback.queues.YouTubeQueue
import com.metrolist.music.ui.component.IconButton
import com.metrolist.music.ui.component.Material3SettingsGroup
import com.metrolist.music.ui.component.Material3SettingsItem
import com.metrolist.music.ui.utils.backToMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale


private data class Contributor(
    val name: String,
    val roleRes: Int,
    val githubHandle: String,
    val avatarUrl: String = "https://github.com/$githubHandle.png",
    val githubUrl: String = "https://github.com/$githubHandle",
    val sponsorUrl: String? = null,
    val polygon: RoundedPolygon? = null,
    val favoriteSongVideoId: String? = null
)

private data class CommunityLink(
    val labelRes: Int,
    val iconRes: Int,
    val url: String
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val leadDeveloper = Contributor(
    name = "Mo Agamy",
    roleRes = R.string.credits_lead_developer,
    githubHandle = "mostafaalagamy",
    polygon = MaterialShapes.Cookie9Sided,
    favoriteSongVideoId = "Mh2JWGWvy_Y"
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val collaborators = listOf(
    Contributor(
        name = "Adriel O'Connel",
        roleRes = R.string.credits_collaborator,
        githubHandle = "adrielGGmotion",
        sponsorUrl = "https://github.com/sponsors/adrielGGmotion",
        polygon = MaterialShapes.Cookie4Sided,
        favoriteSongVideoId = "m2zUrruKjDQ"
    ),
    Contributor(
        name = "Nyx",
        roleRes = R.string.credits_collaborator,
        githubHandle = "nyxiereal",
        sponsorUrl = "https://github.com/sponsors/nyxiereal",
        polygon = MaterialShapes.Cookie12Sided,
        favoriteSongVideoId = "zselaN6zPXw"
    ),
)


private val communityLinks = listOf(
    CommunityLink(
        R.string.credits_discord,
        R.drawable.discord,
        "https://discord.com/invite/zrdbeRG2Mt"
    ),
    CommunityLink(
        R.string.credits_telegram,
        R.drawable.telegram,
        "https://t.me/metrolistapp"
    ),
    CommunityLink(
        R.string.credits_view_repo,
        R.drawable.github,
        "https://github.com/MetrolistGroup/Metrolist"
    ),
    CommunityLink(
        R.string.credits_license_name,
        R.drawable.info,
        "https://github.com/MetrolistGroup/Metrolist/blob/main/LICENSE"
    )
)


private fun handleEasterEggClick(
    clickCount: Int,
    favoriteSongVideoId: String?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    playerConnection: PlayerConnection?,
    wannaPlayStr: String,
    yeahStr: String,
    onCountUpdate: (Int) -> Unit
) {
    if (favoriteSongVideoId != null) {
        val newCount = clickCount + 1

        onCountUpdate(newCount)

        if (newCount >= 3) {
            onCountUpdate(0)

            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = wannaPlayStr,
                    actionLabel = yeahStr,
                    duration = SnackbarDuration.Short
                )

                if (result == SnackbarResult.ActionPerformed) {
                    playerConnection?.playQueue(
                        YouTubeQueue(
                            WatchEndpoint(
                                videoId = favoriteSongVideoId
                            )
                        )
                    )
                }
            }
        }
    }
}


/*
 * Existing avatar implementation.
 *
 * GitHub image:
 * https://github.com/<username>.png
 *
 * Fallback:
 * R.drawable.about_icon
 *
 * Existing shape system is preserved.
 */
@Composable
private fun ContributorAvatar(
    avatarUrl: String,
    sizeDp: Int,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    val fallback = painterResource(
        R.drawable.about_icon
    )

    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.size(sizeDp.dp),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 4.dp,
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = fallback,
            fallback = fallback,
            error = fallback,
        )
    }
}


/*
 * Existing developer social helper.
 * Kept untouched.
 */
@Composable
private fun DeveloperSocials(
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilledTonalButton(
            onClick = {
                uriHandler.openUri("https://metrolist.cc")
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Icon(
                painterResource(R.drawable.language),
                contentDescription = null
            )
        }

        FilledTonalButton(
            onClick = {
                uriHandler.openUri(
                    "https://github.com/mostafaalagamy"
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Icon(
                painterResource(R.drawable.github),
                contentDescription = null
            )
        }

        FilledTonalButton(
            onClick = {
                uriHandler.openUri(
                    "https://www.instagram.com/mostafaalagamy"
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Icon(
                painterResource(R.drawable.instagram),
                contentDescription = null
            )
        }
    }
}


/*
 * ================================================================
 * ABOUT SCREEN
 * ================================================================
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun AboutScreen(
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current

    val playerConnection =
        LocalPlayerConnection.current

    val coroutineScope =
        rememberCoroutineScope()

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    val wannaPlayStr =
        stringResource(
            R.string.wanna_play_favorite_song
        )

    val yeahStr =
        stringResource(
            R.string.yeah
        )

    val windowInsets =
        LocalPlayerAwareWindowInsets.current


    /*
     * ============================================================
     * YOUR PROFILE CONFIGURATION
     * ============================================================
     *
     * Change only these values.
     */

    val developerName = "Subhajit"

    val developerRole = "Developer"

    /*
     * Example:
     *
     * developerGithubUsername = "subhajit"
     *
     * GitHub avatar automatically becomes:
     *
     * https://github.com/subhajit.png
     */

    val developerGithubUsername =
        "YOUR_GITHUB_USERNAME"

    val developerAvatarUrl =
        "https://github.com/$developerGithubUsername.png"


    /*
     * GitHub repository opened by the floating GitHub button.
     */
    val repositoryUrl =
        "https://github.com/MetrolistGroup/Metrolist"


    /*
     * Social links.
     */
    val instagramUrl =
        "https://www.instagram.com/"

    val telegramUrl =
        "https://t.me/"


    /*
     * Existing easter egg state.
     */
    var developerClickCount by remember {
        mutableIntStateOf(0)
    }


    /*
     * ============================================================
     * ROOT
     * ============================================================
     */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    windowInsets.only(
                        WindowInsetsSides.Horizontal +
                            WindowInsetsSides.Bottom
                    )
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 24.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            /*
             * ====================================================
             * TOP BAR
             * ====================================================
             */

            Spacer(
                Modifier.windowInsetsPadding(
                    windowInsets.only(
                        WindowInsetsSides.Top
                    )
                )
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick =
                        navController::navigateUp,
                    onLongClick =
                        navController::backToMain,
                    modifier =
                        Modifier.size(48.dp)
                ) {
                    Icon(
                        painter =
                            painterResource(
                                R.drawable.arrow_back
                            ),
                        contentDescription =
                            stringResource(
                                R.string.cd_back
                            ),
                        modifier =
                            Modifier.size(28.dp)
                    )
                }

                Spacer(
                    Modifier.width(8.dp)
                )

                Text(
                    text =
                        stringResource(
                            R.string.about
                        ),
                    fontSize = 30.sp,
                    fontWeight =
                        FontWeight.Normal,
                    color =
                        MaterialTheme.colorScheme.onBackground,
                    letterSpacing =
                        (-0.6).sp
                )
            }


            /*
             * ====================================================
             * APPLICATION CARD
             * ====================================================
             *
             * Reference:
             *
             * ┌───────────────────────────────────┐
             * │ [ICON]    Zusic                    │
             * │           universal   3.0.0        │
             * └───────────────────────────────────┘
             */

            Spacer(
                Modifier.height(48.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(32.dp),
                color =
                    MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 26.dp,
                            vertical = 24.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                     * Application icon
                     */

                    Box(
                        modifier =
                            Modifier.size(108.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Image(
                            painter =
                                painterResource(
                                    R.drawable.ic_logo_oval
                                ),
                            contentDescription =
                                null,
                            contentScale =
                                ContentScale.Crop,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .clip(
                                        RoundedCornerShape(
                                            27.dp
                                        )
                                    )
                        )

                        Image(
                            painter =
                                painterResource(
                                    R.drawable.about_icon
                                ),
                            contentDescription =
                                stringResource(
                                    R.string.metrolist
                                ),
                            contentScale =
                                ContentScale.Crop,
                            modifier =
                                Modifier.size(82.dp)
                        )
                    }


                    Spacer(
                        Modifier.width(24.dp)
                    )


                    /*
                     * Application details
                     */

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        val appName =
                            stringResource(
                                R.string.metrolist
                            )
                                .lowercase(
                                    Locale.getDefault()
                                )
                                .replaceFirstChar {
                                    if (it.isLowerCase()) {
                                        it.titlecase(
                                            Locale.getDefault()
                                        )
                                    } else {
                                        it.toString()
                                    }
                                }

                        Text(
                            text = appName,
                            fontSize = 42.sp,
                            lineHeight = 44.sp,
                            fontWeight =
                                FontWeight.Black,
                            color =
                                MaterialTheme.colorScheme.onSurface,
                            letterSpacing =
                                (-1.5).sp
                        )

                        Spacer(
                            Modifier.height(5.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    BuildConfig
                                        .ARCHITECTURE
                                        .lowercase(
                                            Locale.getDefault()
                                        ),
                                fontSize = 25.sp,
                                lineHeight = 30.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                letterSpacing =
                                    (-0.7).sp
                            )

                            Spacer(
                                Modifier.width(12.dp)
                            )

                            Text(
                                text =
                                    BuildConfig.VERSION_NAME,
                                fontSize = 25.sp,
                                lineHeight = 30.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                letterSpacing =
                                    (-0.7).sp
                            )
                        }
                    }
                }
            }


            /*
             * ====================================================
             * DEVELOPER IMAGE
             * ====================================================
             *
             * The image is intentionally outside the card.
             *
             * Remote:
             * https://github.com/USERNAME.png
             *
             * Fallback:
             * R.drawable.about_icon
             *
             * Shape:
             * Cookie9Sided
             */

            Spacer(
                Modifier.height(42.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment =
                    Alignment.TopCenter
            ) {

                ContributorAvatar(
                    avatarUrl =
                        developerAvatarUrl,

                    sizeDp = 250,

                    shape =
                        MaterialShapes
                            .Cookie9Sided
                            .toShape(),

                    contentDescription =
                        developerName,

                    modifier =
                        Modifier.align(
                            Alignment.TopCenter
                        ),

                    onClick = {

                        handleEasterEggClick(
                            clickCount =
                                developerClickCount,

                            favoriteSongVideoId =
                                leadDeveloper
                                    .favoriteSongVideoId,

                            coroutineScope =
                                coroutineScope,

                            snackbarHostState =
                                snackbarHostState,

                            playerConnection =
                                playerConnection,

                            wannaPlayStr =
                                wannaPlayStr,

                            yeahStr =
                                yeahStr,

                            onCountUpdate = {
                                developerClickCount =
                                    it
                            }
                        )
                    }
                )
            }


            /*
             * ====================================================
             * DEVELOPER CARD
             * ====================================================
             *
             * Image overlaps this card.
             *
             * GitHub button is floating at top-right.
             */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(
                        y = (-38).dp
                    )
            ) {

                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(168.dp),
                    shape =
                        RoundedCornerShape(32.dp),
                    color =
                        MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 10.dp
                ) {

                    Box(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        /*
                         * Developer details
                         */

                        Column(
                            modifier = Modifier
                                .align(
                                    Alignment.CenterStart
                                )
                                .padding(
                                    start = 36.dp,
                                    end = 110.dp
                                )
                        ) {

                            Text(
                                text =
                                    developerName,
                                fontSize = 40.sp,
                                lineHeight = 44.sp,
                                fontWeight =
                                    FontWeight.Black,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurface,
                                letterSpacing =
                                    (-1.2).sp
                            )

                            Spacer(
                                Modifier.height(2.dp)
                            )

                            Text(
                                text =
                                    developerRole,
                                fontSize = 27.sp,
                                lineHeight = 32.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                letterSpacing =
                                    (-0.7).sp
                            )
                        }


                        /*
                         * ==================================================
                         * GITHUB FLOATING BUTTON
                         * ==================================================
                         */

                        Surface(
                            onClick = {
                                uriHandler.openUri(
                                    repositoryUrl
                                )
                            },
                            modifier = Modifier
                                .align(
                                    Alignment.TopEnd
                                )
                                .offset(
                                    x = (-18).dp,
                                    y = (-34).dp
                                )
                                .size(88.dp),
                            shape =
                                CircleShape,
                            color =
                                MaterialTheme.colorScheme
                                    .primaryContainer,
                            tonalElevation = 0.dp,
                            shadowElevation = 7.dp
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    painter =
                                        painterResource(
                                            R.drawable.github
                                        ),
                                    contentDescription =
                                        "GitHub",
                                    tint =
                                        MaterialTheme.colorScheme
                                            .onPrimaryContainer,
                                    modifier =
                                        Modifier.size(50.dp)
                                )
                            }
                        }
                    }
                }
            }


            /*
             * ====================================================
             * SOCIAL LINKS CARD
             * ====================================================
             *
             * ┌─────────────────────────────┐
             * │ [IG]     Instagram          │
             * │                             │
             * │ [TG]     Telegram           │
             * └─────────────────────────────┘
             */

            Spacer(
                Modifier.height(4.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(34.dp),
                color =
                    MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 10.dp
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            vertical = 14.dp
                        )
                ) {


                    /*
                     * ==================================================
                     * INSTAGRAM
                     * ==================================================
                     */

                    Surface(
                        onClick = {
                            uriHandler.openUri(
                                instagramUrl
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp),
                        color =
                            androidx.compose.ui.graphics
                                .Color.Transparent
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 30.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier =
                                    Modifier.size(64.dp),
                                shape =
                                    RoundedCornerShape(
                                        18.dp
                                    ),
                                color =
                                    MaterialTheme.colorScheme
                                        .primaryContainer,
                                tonalElevation = 0.dp
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        painter =
                                            painterResource(
                                                R.drawable.instagram
                                            ),
                                        contentDescription =
                                            "Instagram",
                                        tint =
                                            MaterialTheme.colorScheme
                                                .onPrimaryContainer,
                                        modifier =
                                            Modifier.size(
                                                34.dp
                                            )
                                    )
                                }
                            }

                            Spacer(
                                Modifier.width(34.dp)
                            )

                            Text(
                                text =
                                    "Instagram",
                                fontSize = 33.sp,
                                lineHeight = 40.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurface,
                                letterSpacing =
                                    (-1).sp
                            )
                        }
                    }


                    /*
                     * ==================================================
                     * TELEGRAM
                     * ==================================================
                     */

                    Surface(
                        onClick = {
                            uriHandler.openUri(
                                telegramUrl
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp),
                        color =
                            androidx.compose.ui.graphics
                                .Color.Transparent
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 30.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier =
                                    Modifier.size(64.dp),
                                shape =
                                    RoundedCornerShape(
                                        18.dp
                                    ),
                                color =
                                    MaterialTheme.colorScheme
                                        .primaryContainer,
                                tonalElevation = 0.dp
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        painter =
                                            painterResource(
                                                R.drawable.telegram
                                            ),
                                        contentDescription =
                                            "Telegram",
                                        tint =
                                            MaterialTheme.colorScheme
                                                .onPrimaryContainer,
                                        modifier =
                                            Modifier.size(
                                                34.dp
                                            )
                                    )
                                }
                            }

                            Spacer(
                                Modifier.width(34.dp)
                            )

                            Text(
                                text =
                                    "Telegram",
                                fontSize = 33.sp,
                                lineHeight = 40.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurface,
                                letterSpacing =
                                    (-1).sp
                            )
                        }
                    }
                }
            }


            /*
             * ====================================================
             * BOTTOM SPACE
             * ====================================================
             */

            Spacer(
                Modifier.height(48.dp)
            )
        }


        /*
         * ========================================================
         * SNACKBAR
         * ========================================================
         */

        SnackbarHost(
            hostState =
                snackbarHostState,
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .windowInsetsPadding(
                    windowInsets.only(
                        WindowInsetsSides.Bottom +
                            WindowInsetsSides.Horizontal
                    )
                )
        )
    }
}