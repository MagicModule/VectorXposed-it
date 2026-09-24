package org.matrix.vector.manager.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.matrix.vector.manager.BuildConfig
import org.matrix.vector.manager.R
import org.matrix.vector.manager.data.github.GitHubRepository
import org.matrix.vector.manager.di.ServiceLocator
import org.matrix.vector.manager.ui.components.CenterTopBar
import org.matrix.vector.manager.ui.components.SettingCategory
import org.matrix.vector.manager.ui.components.SettingSlot
import org.matrix.vector.manager.ui.components.SettingSwitch
import org.matrix.vector.manager.ui.screens.home.HomeAppearanceSheet
import org.matrix.vector.manager.ui.screens.home.HomeViewModel
import org.matrix.vector.ui.SharedSnackbarHost
import org.matrix.vector.ui.locale.LanguageSheet
import org.matrix.vector.manager.ui.theme.VectorLocaleController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenUrl: (String) -> Unit,
    onOpenUpdate: () -> Unit,
    onOpenCanary: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }

    val statusNotification by viewModel.statusNotification.collectAsStateWithLifecycle()
    val hiddenIcon by viewModel.hiddenIcon.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()

    var showAppearance by remember { mutableStateOf(false) }
    var showLanguage by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterTopBar(title = stringResource(R.string.nav_settings)) },
        snackbarHost = { SharedSnackbarHost(snackbars) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
        ) {
            SettingCategory(title = "外观与个性化")
            SettingSlot(
                title = stringResource(R.string.appearance_title),
                description = "深色模式、主题色系及背景微调",
                icon = Icons.Rounded.Palette,
                onClick = { showAppearance = true }
            )
            SettingSlot(
                title = stringResource(R.string.language_title),
                description = "切换应用显示语言",
                icon = Icons.Rounded.Translate,
                onClick = { showLanguage = true }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            SettingCategory(title = "核心与服务")
            SettingSwitch(
                title = stringResource(R.string.status_notification),
                description = "在通知栏实时展示激活状态与运行统计",
                icon = Icons.Rounded.Notifications,
                checked = statusNotification,
                onCheckedChange = { viewModel.setStatusNotification(it) },
                enabled = status.daemonUsable
            )
            SettingSwitch(
                title = stringResource(R.string.force_launcher_icons),
                description = stringResource(R.string.force_launcher_icons_summary),
                icon = Icons.Rounded.VisibilityOff,
                checked = hiddenIcon,
                onCheckedChange = { viewModel.setForcedLauncherIcons(it) }
            )
            SettingSlot(
                title = stringResource(R.string.action_soft_reboot),
                description = "重新启动 Android 运行时（无需重启整机）",
                icon = Icons.Rounded.RestartAlt,
                onClick = {
                    scope.launch {
                        ServiceLocator.daemon.softReboot()
                        snackbars.showSnackbar(context.getString(R.string.action_soft_reboot))
                    }
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            SettingCategory(title = "版本与更新")
            SettingSlot(
                title = "检查框架版本",
                description = "当前核心版本：v${status.versionLabel ?: "2.2"}",
                icon = Icons.Rounded.SystemUpdate,
                onClick = onOpenUpdate
            )
            SettingSlot(
                title = "Canary 尝鲜测试",
                description = "体验最新的实验性提交构建",
                icon = Icons.Rounded.Science,
                onClick = onOpenCanary
            )
            SettingSlot(
                title = "项目源代码",
                description = GitHubRepository.REPO_URL,
                icon = Icons.Rounded.Code,
                onClick = { onOpenUrl(GitHubRepository.REPO_URL) }
            )
            SettingSlot(
                title = "关于 Vector-it",
                description = "版本 ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) · 作者 JavSaia",
                icon = Icons.Rounded.Info,
                onClick = {}
            )
        }
    }

    if (showAppearance) {
        HomeAppearanceSheet(
            onDismiss = { showAppearance = false },
        )
    }

    if (showLanguage) {
        LanguageSheet(
            controller = VectorLocaleController,
            onDismiss = { showLanguage = false },
            onHelpTranslate = { onOpenUrl("https://crowdin.com") },
        )
    }
}
