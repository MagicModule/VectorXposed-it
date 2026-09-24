package org.matrix.vector.manager.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.matrix.vector.manager.data.repository.VectorLogSource
import org.matrix.vector.manager.data.repository.VectorStoreInstallHost
import org.matrix.vector.manager.di.ServiceLocator
import org.matrix.vector.manager.ui.navigation.Canary
import org.matrix.vector.manager.ui.navigation.CrashTrace
import org.matrix.vector.manager.ui.navigation.DeepLink
import org.matrix.vector.manager.ui.navigation.FrameworkUpdate
import org.matrix.vector.manager.ui.navigation.LogTrace
import org.matrix.vector.manager.ui.navigation.Scope
import org.matrix.vector.manager.ui.navigation.StoreDetail
import org.matrix.vector.manager.ui.navigation.SystemStatus
import org.matrix.vector.manager.ui.navigation.TOP_LEVEL_DESTINATIONS
import org.matrix.vector.manager.ui.navigation.TopLevelRoute
import org.matrix.vector.manager.ui.navigation.Troubleshoot
import org.matrix.vector.manager.ui.navigation.VectorNavPanelStore
import org.matrix.vector.manager.ui.navigation.Web
import org.matrix.vector.manager.ui.screens.canary.CanaryScreen
import org.matrix.vector.manager.ui.screens.home.CrashTraceScreen
import org.matrix.vector.manager.ui.screens.home.HomeScreen
import org.matrix.vector.manager.ui.screens.home.SystemStatusScreen
import org.matrix.vector.manager.ui.screens.modules.ModulesScreen
import org.matrix.vector.manager.ui.screens.modules.ScopeScreen
import org.matrix.vector.manager.ui.screens.report.TroubleshootScreen
import org.matrix.vector.manager.ui.screens.settings.SettingsScreen
import org.matrix.vector.manager.ui.screens.update.FrameworkUpdateScreen
import org.matrix.vector.manager.ui.screens.web.WebScreen
import org.matrix.vector.manager.ui.screens.web.fetchStoreSubresource
import org.matrix.vector.manager.ui.screens.web.forWebView
import org.matrix.vector.ui.logs.LogTraceScreen
import org.matrix.vector.ui.logs.LogsScreen
import org.matrix.vector.ui.navigation.LocalNavigator
import org.matrix.vector.ui.navigation.Navigator
import org.matrix.vector.ui.navigation.rememberNavigator
import org.matrix.vector.ui.store.RepoDetailsScreen
import org.matrix.vector.ui.store.RepoScreen

/**
 * Standard LSPatch-style app shell.
 *
 * Employs a clean Material 3 Scaffold with standard NavigationBar.
 * Avoids any floating overlays or gesture-hijacking panels, guaranteeing
 * seamless predictive back gestures on Android 14+.
 */
@Composable
fun VectorApp() {
    val navigator = rememberNavigator(VectorNavPanelStore, TOP_LEVEL_DESTINATIONS)

    val pending by DeepLink.pending.collectAsStateWithLifecycle()
    LaunchedEffect(pending) {
        val destination = DeepLink.consume() ?: return@LaunchedEffect
        if (navigator.current == (destination.detail ?: destination.tab)) return@LaunchedEffect
        navigator.switchTo(destination.tab)
        destination.detail?.let { navigator.go(it) }
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        val atRoot = !navigator.canGoBack

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (atRoot) {
                    NavigationBar {
                        TOP_LEVEL_DESTINATIONS.forEach { dest ->
                            val selected = navigator.currentTopLevel == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigator.switchTo(dest.route) },
                                icon = { Icon(dest.icon, contentDescription = stringResource(dest.labelRes)) },
                                label = { Text(stringResource(dest.labelRes)) },
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                NavDisplay(
                    backStack = navigator.backStack,
                    onBack = { navigator.back() },
                    entryDecorators =
                        listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth / 3 },
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(150))
                    },
                    popTransitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth / 3 },
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(150)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    },
                    predictivePopTransitionSpec = { _ ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth / 3 },
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(150)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    },
                    entryProvider = entryProvider { registerRoutes(navigator) },
                )
            }
        }
    }
}

/**
 * Every destination, registered.
 */
private fun EntryProviderScope<NavKey>.registerRoutes(navigator: Navigator) {
    entry<TopLevelRoute.Home> {
        HomeScreen(
            onOpenStatus = { navigator.go(SystemStatus) },
            onOpenUrl = { url -> navigator.go(Web(url)) },
            onOpenCanary = { navigator.go(Canary) },
            onOpenReport = { navigator.go(Troubleshoot) },
            onOpenUpdate = { navigator.go(FrameworkUpdate()) },
            onOpenModules = { navigator.switchTo(TopLevelRoute.Modules) },
        )
    }
    entry<TopLevelRoute.Modules> {
        ModulesScreen(
            onModuleClick = { packageName, userId -> navigator.go(Scope(packageName, userId)) },
            onOpenStore = { packageName -> navigator.go(StoreDetail(packageName)) },
        )
    }
    entry<TopLevelRoute.Store> {
        RepoScreen(
            onModuleClick = { packageName -> navigator.go(StoreDetail(packageName)) },
            dataSource = ServiceLocator.store,
            settings = ServiceLocator.settings,
        )
    }
    entry<TopLevelRoute.Logs> {
        val logSource = remember { VectorLogSource() }
        LogsScreen(source = logSource, onOpenTrace = { text -> navigator.go(LogTrace(text)) })
    }
    entry<TopLevelRoute.Settings> {
        SettingsScreen(
            onOpenUrl = { url -> navigator.go(Web(url)) },
            onOpenUpdate = { navigator.go(FrameworkUpdate()) },
            onOpenCanary = { navigator.go(Canary) },
        )
    }

    entry<Scope> { route ->
        ScopeScreen(
            packageName = route.packageName,
            userId = route.userId,
            onNavigateBack = { navigator.back() },
        )
    }
    entry<StoreDetail> { route ->
        RepoDetailsScreen(
            packageName = route.packageName,
            onNavigateBack = { navigator.back() },
            onOpenUrl = { url -> navigator.go(Web(url)) },
            dataSource = ServiceLocator.store,
            settings = ServiceLocator.settings,
            host = remember(route.packageName) { VectorStoreInstallHost(route.packageName) },
            fetchSubresource = { fetchStoreSubresource(ServiceLocator.http, it) },
            contextForWebView = { ctx, dark -> ctx.forWebView(dark) },
        )
    }
    entry<SystemStatus> {
        SystemStatusScreen(
            onNavigateBack = { navigator.back() },
            onOpenCrash = { navigator.go(CrashTrace) },
        )
    }
    entry<CrashTrace> { CrashTraceScreen(onNavigateBack = { navigator.back() }) }
    entry<LogTrace> { route ->
        LogTraceScreen(text = route.text, onNavigateBack = { navigator.back() })
    }
    entry<Troubleshoot> {
        TroubleshootScreen(
            onNavigateBack = { navigator.back() },
            onOpenUrl = { url -> navigator.go(Web(url)) },
            onOpenCanary = { navigator.go(Canary) },
        )
    }
    entry<Canary> {
        CanaryScreen(
            onNavigateBack = { navigator.back() },
            onOpenUrl = { url -> navigator.go(Web(url)) },
            onInstall = { versionCode -> navigator.go(FrameworkUpdate(versionCode)) },
            onOpenReport = { navigator.go(Troubleshoot) },
        )
    }
    entry<FrameworkUpdate> { route ->
        FrameworkUpdateScreen(
            openOnVersionCode = route.versionCode.takeIf { it > 0 },
            onNavigateBack = { navigator.back() },
            onOpenUrl = { url -> navigator.go(Web(url)) },
        )
    }
    entry<Web> { route -> WebScreen(url = route.url, onNavigateBack = { navigator.back() }) }
}
