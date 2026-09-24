package org.matrix.vector.manager.ui.screens.home

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.ExperimentalFoundationApi
import org.matrix.vector.ui.contextClickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallSplit
import androidx.compose.material.icons.automirrored.rounded.AddToHomeScreen
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material3.InputChip
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowDown
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowUp
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.launch
import org.matrix.vector.manager.ui.theme.LocalizedOverlay
import org.matrix.vector.manager.R
import org.matrix.vector.manager.ui.theme.CROWDIN_URL
import org.matrix.vector.manager.ui.theme.VectorLocaleController
import org.matrix.vector.ui.locale.LanguageSheet
import org.matrix.vector.ui.locale.currentLocale
import org.matrix.vector.manager.di.ServiceLocator
import org.matrix.vector.ui.SharedAlertDialog
import org.matrix.vector.ui.SharedSnackbarHost
import org.matrix.vector.ui.show
import org.matrix.vector.manager.data.github.CommunityFeed
import org.matrix.vector.manager.data.github.FeedItem
import org.matrix.vector.manager.data.github.FeedLayout
import org.matrix.vector.manager.data.github.Contributor
import org.matrix.vector.manager.data.github.GitHubRepository
import org.matrix.vector.manager.data.github.TimelineCommit
import org.matrix.vector.manager.ui.components.BotBundleRow
import org.matrix.vector.manager.ui.components.CommitRow
import org.matrix.vector.manager.ui.components.InstalledMarkerRow
import org.matrix.vector.manager.ui.components.MonthMarkerRow
import org.matrix.vector.manager.ui.components.GapRow
import org.matrix.vector.manager.ui.components.HistoryFootRow
import org.matrix.vector.manager.ui.components.ContributorAvatar
import org.matrix.vector.manager.ui.components.TakePartSection
import org.matrix.vector.manager.ui.components.statusWordRes
import org.matrix.vector.ui.UpdatableVersion
import org.matrix.vector.manager.ui.components.FrameworkState
import org.matrix.vector.manager.ui.screens.splash.WingedVictory
import org.matrix.vector.ui.RepoStatsRow
import org.matrix.vector.ui.theme.Mono

/**
 * Home is the front page of the *project*, not only of the app.
 *
 * A framework manager is opened by every user, and Vector is built by volunteers, so this screen
 * spends its space on the two questions that matter on opening it: is the framework healthy (one
 * line), and what has the project been doing (everything else).
 *
 * The activity window is a span of time rather than "the latest N commits" — six months by default,
 * and the reader's to change from the appearance sheet. In a quiet stretch the page honestly reads
 * *7 commits by 4 people*, which is real information about the project; a rolling N would hide that.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenStatus: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenCanary: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenUpdate: () -> Unit,
    onOpenModules: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    val enabledModules by ServiceLocator.modules.enabledModulesState.collectAsStateWithLifecycle()
    val enabledModulesCount = enabledModules.size
    val feed by viewModel.feed.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val openExternally by viewModel.openLinksExternally.collectAsStateWithLifecycle()
    val feedItems by viewModel.feedItems.collectAsStateWithLifecycle()
    val loadingHistory by viewModel.loadingHistory.collectAsStateWithLifecycle()
    val historyStalled by viewModel.historyStalled.collectAsStateWithLifecycle()
    val authorFilter by viewModel.authorFilter.collectAsStateWithLifecycle()
    val windowChanged by viewModel.windowChanged.collectAsStateWithLifecycle()
    val frameworkUpdate by viewModel.frameworkUpdate.collectAsStateWithLifecycle()
    val presence by viewModel.presence.collectAsStateWithLifecycle()
    val promptDismissed by viewModel.launcherPromptDismissed.collectAsStateWithLifecycle()
    val hintStatus by viewModel.statusBadgeHint.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showSplash by rememberSaveable { mutableStateOf(false) }
    var showAppearance by rememberSaveable { mutableStateOf(false) }
    var showLanguage by rememberSaveable { mutableStateOf(false) }
    // Answered or waved away once per visit, not once per return to Home. Saved so that a rotation
    // does not put a dialog back in front of someone who has just dismissed it.
    var showLauncherPrompt by rememberSaveable { mutableStateOf(true) }

    // The status screen has its own copy of this ViewModel — a nav destination is its own store —
    // so a shortcut pinned or an app installed from there is invisible to this one until it is
    // asked again. Coming back to Home is when it is worth asking, and it is also the only moment
    // the badge's hint can start running again, so today's tally of it is re-cut here too.
    LaunchedEffect(Unit) {
        viewModel.refreshPresence()
        viewModel.refreshStatusBadgeHint()
    }

    // Four taps on the wordmark, with the remaining count announced from the second. Two taps
    // could be an accident; past that the reader is clearly poking at it, so the app plays along
    // rather than keeping a secret nobody would find.
    var brandTaps by remember { mutableStateOf(0) }
    var lastBrandTapAt by remember { mutableStateOf(0L) }
    val twoMore = stringResource(R.string.egg_two_more)
    val oneMore = stringResource(R.string.egg_one_more)
    val haptics = LocalHapticFeedback.current
    val snackbars = remember { SnackbarHostState() }
    val eggScope = rememberCoroutineScope()

    fun onBrandTap() {
        val now = System.currentTimeMillis()
        brandTaps = if (now - lastBrandTapAt > BRAND_TAP_WINDOW_MS) 1 else brandTaps + 1
        lastBrandTapAt = now
        when (brandTaps) {
            // The app's own snackbar, not a platform toast. A toast is drawn by the system in the
            // system's style and ignores the theme entirely, which on a screen whose whole point
            // is the surface underneath it reads as a message from another app.
            2 -> eggScope.launch { snackbars.show(twoMore) }
            3 -> eggScope.launch { snackbars.show(oneMore) }
            BRAND_TAPS_TO_SUMMON -> {
                brandTaps = 0
                lastBrandTapAt = 0L
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                showSplash = true
            }
        }
    }

    // Every GitHub link goes through the in-app viewer by default; the setting sends them to a
    // browser instead for users who would rather stay in one.
    fun open(url: String) {
        if (openExternally) {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (_: ActivityNotFoundException) {
                // Nothing on the device took the intent. Falling back to the built-in viewer beats
                // a link tap that does nothing at all.
                onOpenUrl(url)
            }
        } else {
            onOpenUrl(url)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = onOpenStatus) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = stringResource(R.string.status_open_details),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Rounded.MoreVert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.appearance_title)) },
                                onClick = {
                                    showMenu = false
                                    showAppearance = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Palette, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.language_title)) },
                                onClick = {
                                    showMenu = false
                                    showLanguage = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Translate, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SharedSnackbarHost(snackbars) },
    ) { padding ->
        val listState = rememberLazyListState()
        var isPullRefreshing by remember { mutableStateOf(false) }

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isPullRefreshing,
                onRefresh = {
                    eggScope.launch {
                        isPullRefreshing = true
                        try {
                            viewModel.refreshPresence()
                            kotlinx.coroutines.delay(400)
                        } finally {
                            isPullRefreshing = false
                        }
                    }
                },
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        LsPatchStatusCard(
                            status = status,
                            onOpenStatus = onOpenStatus,
                            onSoftReboot = {
                                eggScope.launch {
                                    ServiceLocator.daemon.softReboot()
                                    snackbars.show(context.getString(R.string.action_soft_reboot))
                                }
                            },
                        )
                    }

                    item {
                        LsPatchModulesCard(
                            enabledCount = enabledModulesCount,
                            onClick = onOpenModules,
                        )
                    }

                    item {
                        LsPatchInfoCard(
                            status = status,
                            onCopy = { infoText ->
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.app_name), infoText))
                                eggScope.launch {
                                    snackbars.show(context.getString(R.string.home_copied_info))
                                }
                            }
                        )
                    }

                    item {
                        LsPatchSupportCard(
                            onOpenUrl = ::open,
                            onOpenReport = onOpenReport,
                            onOpenUpdate = onOpenUpdate,
                            onOpenCanary = onOpenCanary,
                            hasUpdate = frameworkUpdate.hasUpdate,
                            onSoftReboot = {
                                eggScope.launch {
                                    ServiceLocator.daemon.softReboot()
                                    snackbars.show(context.getString(R.string.action_soft_reboot))
                                }
                            },
                        )
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }

    if (showLanguage) {
        LanguageSheet(
            controller = VectorLocaleController,
            onDismiss = { showLanguage = false },
            onHelpTranslate = { open(CROWDIN_URL) },
            onOpenUrl = ::open,
        )
    }

    if (showAppearance) {
        HomeAppearanceSheet(onDismiss = { showAppearance = false })
    }

    // Nothing in the launcher points at a parasitic manager, so someone who reached this screen
    // through the root manager's action button has no way of finding it again — which is what #815
    // reported. Asked once, on the first launch that could act on the answer, and never again after
    // "Don't ask again" or after either remedy has been applied. Dismissing it any other way means
    // "later": the offer stays on the status page and returns on the next launch.
    if (
        showLauncherPrompt &&
            presence.unreachable &&
            !promptDismissed &&
            // With no usable daemon there is no APK to install and bigger problems to report
            // first.
            status.daemonUsable
    ) {
        LauncherPrompt(
            shortcutSupported = presence.shortcutSupported,
            onCreateShortcut = {
                showLauncherPrompt = false
                viewModel.requestShortcut()
            },
            onInstall = {
                showLauncherPrompt = false
                viewModel.installManagerApp()
            },
            onNever = {
                showLauncherPrompt = false
                viewModel.dismissLauncherPrompt()
            },
            onLater = { showLauncherPrompt = false },
        )
    }

    // Summoned by four taps on the wordmark. A dialog rather than an overlay inside the content,
    // so it covers the navigation bar too — a splash framed by app chrome is not a splash.
    if (showSplash) {
        Dialog(
            onDismissRequest = { showSplash = false },
            properties =
                DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true),
        ) {
LocalizedOverlay {

            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            showSplash = false
                        }
            ) {
                WingedVictory()
            }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2800)
                showSplash = false
            }
        }
}
    }
}

/**
 * The one prompt Vector shows unasked, and only when there is genuinely no way back in.
 *
 * Two buttons rather than four: the primary is whichever remedy this device can actually apply, and
 * the other is the refusal. "Later" is the dialog's ordinary dismissal — tapping away or pressing
 * back — because that is already what dismissing a dialog means, and a third button spelling it out
 * would crowd out the two that do something.
 */
@Composable
private fun LauncherPrompt(
    shortcutSupported: Boolean,
    onCreateShortcut: () -> Unit,
    onInstall: () -> Unit,
    onNever: () -> Unit,
    onLater: () -> Unit,
) {
    SharedAlertDialog(
        onDismissRequest = onLater,
        icon = { Icon(Icons.AutoMirrored.Rounded.AddToHomeScreen, contentDescription = null) },
        title = { Text(stringResource(R.string.launcher_prompt_title)) },
        text = { Text(stringResource(R.string.launcher_prompt_body)) },
        confirmButton = {
            // A launcher that refuses pin requests leaves installing as the only remedy, so that is
            // what the button offers rather than one that would visibly do nothing.
            if (shortcutSupported) {
                TextButton(onClick = onCreateShortcut) {
                    Text(stringResource(R.string.launcher_shortcut_create))
                }
            } else {
                TextButton(onClick = onInstall) {
                    Text(stringResource(R.string.launcher_install_action))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onNever) { Text(stringResource(R.string.launcher_prompt_never)) }
        },
    )
}

/**
 * The window's activity: a headline, the people, then the rail.
 *
 * People come before commits deliberately. The section exists to make participation visible, and
 * faces do that faster than a list of subjects does.
 */
private fun androidx.compose.foundation.lazy.LazyListScope.communitySection(
    feed: CommunityFeed,
    items: List<FeedItem>,
    loadingHistory: Boolean,
    historyStalled: Boolean,
    windowChanged: Boolean,
    authorFilter: Set<String>,
    onLoadMoreHistory: () -> Unit,
    onToggleAuthor: (String) -> Unit,
    onClearAuthors: () -> Unit,
    onOpenCommit: (TimelineCommit) -> Unit,
    onOpenPullRequest: (Int) -> Unit,
    onOpenProfile: (Contributor) -> Unit,
) {
    item { QuarterHeadline(feed, windowChanged) }

    if (feed.contributors.isNotEmpty()) {
        item {
            ContributorRow(
                contributors = feed.contributors,
                selected = authorFilter,
                onClick = onOpenProfile,
                onLongClick = { onToggleAuthor(it.login) },
            )
            Spacer(Modifier.height(if (authorFilter.isEmpty()) 20.dp else 10.dp))
        }
    }

    if (authorFilter.isNotEmpty()) {
        item(key = "author-filter") {
            AuthorFilterBar(
                // Driven by the filter set rather than by the contributor row: a name held from a
                // commit row may belong to someone with no place in the row at all — a bot, or
                // somebody whose only commit is outside the current window — and a filter you
                // cannot see is a filter you cannot lift.
                logins =
                    authorFilter.map { key ->
                        feed.contributors.firstOrNull { it.login.lowercase() == key }?.login ?: key
                    },
                // Counted through the bot bundles, not around them: a bundle stands for several
                // commits, and a number that disagreed with the same person's tally in the row
                // above it would look like one of the two being wrong.
                commits =
                    items.sumOf { item ->
                        when (item) {
                            is FeedItem.Commit -> 1
                            is FeedItem.Bots -> item.count
                            else -> 0
                        }
                    },
                onRemove = onToggleAuthor,
                onClear = onClearAuthors,
            )
        }
    }

    items(items = items, key = { it.key() }) { entry ->
        when (entry) {
            is FeedItem.Commit ->
                CommitRow(
                    commit = entry.commit,
                    isFirst = entry.isFirst,
                    isLast = entry.isLast,
                    onOpenCommit = onOpenCommit,
                    onOpenPullRequest = onOpenPullRequest,
                    onFilterAuthor = onToggleAuthor,
                )
            is FeedItem.Gap ->
                GapRow(
                    days = entry.days,
                    heightDp = FeedLayout.railHeightDp(entry.days),
                    showLabel = entry.days >= FeedLayout.QUIET_THRESHOLD_DAYS,
                )
            is FeedItem.InstalledMarker ->
                InstalledMarkerRow(
                    versionCode = entry.versionCode,
                    commitsAhead = entry.commitsAhead,
                    aheadOfMaster = entry.aheadOfMaster,
                )
            is FeedItem.MonthMarker ->
                MonthMarkerRow(entry.month, entry.year, entry.commits, entry.people)
            is FeedItem.Bots -> BotBundle(count = entry.count, commits = entry.commits)
        }
    }

    if (items.isNotEmpty()) {
        item(key = "history-foot") {
            val locale = currentLocale()
            // Only claimed when the whole project is in hand — the count from the `Link` header is
            // every commit on the default branch, ever, so holding that many means the row below is
            // genuinely the first one. A window that merely reached its own start says nothing,
            // because history continues past it.
            val wholeProject = feed.totalCommits > 0 && feed.commitCount >= feed.totalCommits
            HistoryFootRow(
                loading = loadingHistory,
                hasMore = feed.hasMoreHistory,
                stalled = historyStalled,
                beginningDate =
                    if (!wholeProject) null
                    else
                        DateFormat.getDateInstance(DateFormat.LONG, locale)
                            .format(Date(feed.windowStartEpochSeconds * 1000)),
                windowCovered = feed.windowCovered,
                onReachEnd = onLoadMoreHistory,
                onRetry = onLoadMoreHistory,
                autoFetch = authorFilter.isEmpty(),
            )
        }
    }
}

/** Stable identity per row, so a refresh does not rebuild the whole rail. */
private fun FeedItem.key(): String =
    when (this) {
        is FeedItem.Commit -> "c:${commit.sha}"
        is FeedItem.Gap -> "g:$afterSha"
        is FeedItem.InstalledMarker -> "installed"
        is FeedItem.MonthMarker -> "m:$key"
        is FeedItem.Bots -> "bots"
    }

@Composable
private fun BotBundle(count: Int, commits: List<TimelineCommit>) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    BotBundleRow(
        count = count,
        expanded = expanded,
        onToggle = { expanded = !expanded },
        isLast = true,
    ) {
        commits.forEach { c ->
            Row(modifier = Modifier.padding(start = 36.dp, bottom = 10.dp)) {
                Text(
                    text = c.subject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun QuarterHeadline(feed: CommunityFeed, windowChanged: Boolean) {
    val people = feed.contributors.size
    val context = LocalContext.current
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(
            text = stringResource(R.string.home_quarter_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(2.dp))
        if (!feed.loaded) {
            Text(
                text = stringResource(R.string.home_loading_activity),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (feed.isEmpty) {
            Text(
                text = stringResource(R.string.home_no_activity),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            val commits =
                context.resources.getQuantityString(
                    R.plurals.home_commit_count,
                    feed.commitCount,
                    feed.commitCount,
                )
            val by = context.resources.getQuantityString(R.plurals.home_people_count, people, people)
            val since =
                DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale())
                    .format(Date(feed.windowStartEpochSeconds * 1000))
            Text(
                text = "$commits $by  ·  ${stringResource(R.string.home_since, since)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        // Three different situations, and only one of them is a failure. Home reads the feed from
        // disk on most launches *on purpose* — the window moves a few times a week, and
        // revalidating every time spends battery and rate limit to redraw identical rows — so a
        // cached answer must not be reported as "could not reach GitHub".
        //
        // A fourth situation, and the only one that asks for something: the window was just
        // changed, so what is on screen was re-cut from disk and may not reach as far as the new
        // window does. It takes precedence over the other three because it is the newest fact and
        // the only actionable one.
        if (windowChanged || feed.offline || feed.fromCache) {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when {
                        windowChanged -> Icons.Rounded.Refresh
                        feed.offline -> Icons.Rounded.CloudOff
                        else -> Icons.Rounded.Bedtime
                    },
                    contentDescription = null,
                    modifier = Modifier.height(14.dp),
                    tint =
                        if (windowChanged) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text =
                        stringResource(
                            when {
                                windowChanged -> R.string.home_window_changed
                                feed.offline -> R.string.home_offline
                                else -> R.string.home_resting
                            }
                        ),
                    style = MaterialTheme.typography.labelSmall,
                    color =
                        if (windowChanged) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Two ways back through a feed that can be thousands of commits long.
 *
 * Hidden until they are needed, which is the only way a persistent control earns its place on a
 * screen whose subject is the content behind it. "Needed" is defined as having scrolled past the
 * headline — before that, the top is already on screen and a button to reach it is furniture.
 *
 * The page-down button is the answer to a rail that keeps growing as it is read: with history
 * arriving in chunks, a flick lands somewhere arbitrary and the reader has to flick again. One
 * viewport at a time is a predictable unit, and it stops existing at the end of the list rather
 * than sitting there doing nothing.
 *
 * Deliberately small and tonal rather than a floating action button: nothing here is *the* action
 * of the screen, and a full FAB would claim to be.
 */
@Composable
private fun ScrollControls(listState: LazyListState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    // Past the headline, so the pair appears at the moment the top of the feed stops being
    // reachable by eye.
    val visible by remember { derivedStateOf { listState.firstVisibleItemIndex >= 2 } }
    val atEnd by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()
            last != null && last.index >= info.totalItemsCount - 1
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedVisibility(visible = !atEnd, enter = fadeIn(), exit = fadeOut()) {
                FilledTonalIconButton(
                    onClick = {
                        scope.launch {
                            // A shade under a full screen, so the line the reader stopped on stays
                            // visible at the top and the two screens are stitched rather than cut.
                            listState.animateScrollBy(
                                listState.layoutInfo.viewportSize.height * 0.9f
                            )
                        }
                    },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        Icons.Rounded.KeyboardDoubleArrowDown,
                        contentDescription = stringResource(R.string.home_scroll_down),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            FilledTonalIconButton(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    Icons.Rounded.KeyboardDoubleArrowUp,
                    contentDescription = stringResource(R.string.home_scroll_top),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * What filter mode looks like: who is being shown, how much of the history that is, and the way out.
 *
 * It is a bar rather than a badge on the header because it has to carry the exit. A filtered list
 * that gives no visible way back is the sort of state people escape by force-quitting the app, and
 * the gesture that entered it — a long press, somewhere up the row — is not one anyone should have
 * to rediscover.
 *
 * Each name is its own chip with its own dismiss, so removing the second of two people is one tap
 * rather than clearing and starting again. Removing the last one leaves the set empty, which *is*
 * the unfiltered state — there is no separate "off" to get out of step with.
 */
@Composable
private fun AuthorFilterBar(
    logins: List<String>,
    commits: Int,
    onRemove: (String) -> Unit,
    onClear: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Rounded.FilterAlt,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = pluralStringResource(R.plurals.home_commit_count, commits, commits),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onClear, contentPadding = PaddingValues(horizontal = 10.dp)) {
                Text(stringResource(R.string.home_filter_clear))
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            logins.forEach { login ->
                InputChip(
                    selected = true,
                    onClick = { onRemove(login) },
                    label = { Text(login, maxLines = 1) },
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.home_filter_remove, login),
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }
        }
    }
}

/**
 * How the contributor row is ordered.
 *
 * Recency is not a lesser ordering, it is a different kind of credit: by volume the maintainer is
 * first forever and the row never moves, which is accurate and says nothing new; by recency the
 * person who last landed something leads, and a first contribution is visible the day it happens.
 * Both break ties with the other, so neither is ever arbitrary.
 */
enum class ContributorOrder(val key: String, val labelRes: Int) {
    Commits("commits", R.string.home_contributors_by_commits),
    Recent("recent", R.string.home_contributors_by_recent);

    fun sort(people: List<Contributor>): List<Contributor> =
        when (this) {
            Commits ->
                people.sortedWith(
                    compareByDescending<Contributor> { it.commits }
                        .thenByDescending { it.lastEpochSeconds }
                        .thenBy { it.login }
                )
            Recent ->
                people.sortedWith(
                    compareByDescending<Contributor> { it.lastEpochSeconds }
                        .thenByDescending { it.commits }
                        .thenBy { it.login }
                )
        }

    companion object {
        fun from(key: String?): ContributorOrder = entries.firstOrNull { it.key == key } ?: Commits
    }
}

/**
 * The people of the window, the leader wreathed.
 *
 * Scoped to the window rather than all time on purpose: an all-time leaderboard is a monument and
 * never changes, so nobody reads it twice. One cut to a few months moves, and a first-time
 * contributor appears on it immediately.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContributorRow(
    contributors: List<Contributor>,
    selected: Set<String>,
    onClick: (Contributor) -> Unit,
    onLongClick: (Contributor) -> Unit,
) {
    // The preference is read here rather than threaded down from the screen: the row is emitted
    // from a LazyListScope extension, which is not a composable and has no state to hand over.
    // Sorting here also means changing the setting reorders the row immediately, with no re-fetch
    // of a feed that has not changed.
    val order =
        ContributorOrder.from(
            ServiceLocator.settings.contributorOrder.collectAsStateWithLifecycle().value
        )
    val people = remember(contributors, order) { order.sort(contributors) }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(people, key = { it.login }) { person ->
            val leader = person == people.first()
            // A co-author signed with a plain email address has no GitHub identity to open, so the
            // tap is withheld and the avatar dimmed rather than offering something that goes
            // nowhere. They are still shown, still counted, and still filterable — they have
            // commits like anyone else — so the long press is offered to the whole row.
            val hasProfile = !person.profileUrl.isNullOrBlank()
            val picked = person.login.lowercase() in selected
            val haptics = LocalHapticFeedback.current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier.contextClickable(
                            onClick = { if (hasProfile) onClick(person) },
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongClick(person)
                            },
                        )
                        .alpha(
                            when {
                                // In filter mode the people not being shown step back, so the row
                                // says at a glance whose rail is on screen.
                                selected.isNotEmpty() && !picked -> 0.35f
                                hasProfile -> 1f
                                else -> 0.45f
                            }
                        )
                        .width(72.dp),
            ) {
                ContributorAvatar(
                    login = person.login,
                    avatarUrl = person.avatarUrl,
                    size = 44.dp,
                    laurelled = leader,
                    selected = picked,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = person.login,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = person.commits.toString(),
                    style = Mono,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProjectFooter(feed: CommunityFeed, onClick: () -> Unit) {
    val repo = feed.repo ?: return
    // The shared project footer — the same row LSPatch's Home shows, over its own repo.
    RepoStatsRow(
        stars = repo.stars,
        forks = repo.forks,
        openIssues = repo.openIssues,
        license = repo.license?.spdxId,
        onClick = onClick,
    )
}

/** Taps must land within this window of each other to count towards the same run. */
private const val BRAND_TAP_WINDOW_MS = 2600L

private const val BRAND_TAPS_TO_SUMMON = 4

@Composable
private fun LsPatchStatusCard(
    status: FrameworkStatus,
    onOpenStatus: () -> Unit,
    onSoftReboot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val active = status.state == FrameworkState.Active
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenStatus() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (active) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (active) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = stringResource(R.string.status_active),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Column(Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(
                        text = "Vector-it " + stringResource(R.string.status_active),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "API ${status.apiVersion ?: 102} · Core v${status.versionLabel ?: "2.2"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                    )
                }
            } else {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = stringResource(status.state.statusWordRes()),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(32.dp)
                )
                Column(Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_name) + " " + stringResource(status.state.statusWordRes()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.home_inactive_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LsPatchModulesCard(
    enabledCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                Icons.Rounded.Extension,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.nav_modules),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text =
                        if (enabledCount > 0) {
                            stringResource(R.plurals.home_modules_active, enabledCount, enabledCount)
                        } else {
                            stringResource(R.string.home_modules_manage)
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LsPatchInfoCard(
    status: FrameworkStatus,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val apiVersion = if (Build.VERSION.PREVIEW_SDK_INT != 0) {
        "${Build.VERSION.CODENAME} Preview (API ${Build.VERSION.PREVIEW_SDK_INT})"
    } else {
        "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }
    val device = buildString {
        append(Build.MANUFACTURER.replaceFirstChar { it.uppercase() })
        if (Build.BRAND != Build.MANUFACTURER) {
            append(" " + Build.BRAND.replaceFirstChar { it.uppercase() })
        }
        append(" " + Build.MODEL)
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val contents = StringBuilder()
            val infoRow: @Composable (String, String) -> Unit = { title, value ->
                contents.appendLine("$title: $value")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }

            infoRow(stringResource(R.string.info_api_version), "${status.apiVersion ?: 102}")
            infoRow(stringResource(R.string.info_framework_version), "${status.versionName ?: "2.2"} (${status.versionCode})")
            infoRow(stringResource(R.string.info_selinux), if (status.sepolicyLoaded) "Enforcing" else "Permissive")
            infoRow(
                stringResource(R.string.info_system_server),
                stringResource(if (status.systemServerInjected) R.string.info_injected else R.string.info_not_injected),
            )
            infoRow(stringResource(R.string.info_android), apiVersion)
            infoRow(stringResource(R.string.info_device), device)
            infoRow(stringResource(R.string.info_abi), Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a")

            Spacer(Modifier.height(4.dp))
            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = { onCopy(contents.toString().trim()) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Rounded.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(android.R.string.copy), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun LsPatchSupportCard(
    onOpenUrl: (String) -> Unit,
    onOpenReport: () -> Unit,
    onOpenUpdate: () -> Unit,
    onOpenCanary: () -> Unit,
    hasUpdate: Boolean,
    onSoftReboot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.home_quick_actions),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onOpenUrl(GitHubRepository.REPO_URL) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("GitHub", style = MaterialTheme.typography.labelLarge)
                }
                OutlinedButton(
                    onClick = onOpenReport,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(stringResource(R.string.home_diagnostics), style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSoftReboot,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(stringResource(R.string.action_soft_reboot), style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = onOpenUpdate,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(
                        if (hasUpdate) stringResource(R.string.home_update_available)
                        else stringResource(R.string.home_check_update),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
