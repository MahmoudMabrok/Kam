package tools.mo3ta.kam.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import tools.mo3ta.kam.data.CityRepository
import tools.mo3ta.kam.data.PreferenceManager
import tools.mo3ta.kam.ui.revamp.HomeScreen
import tools.mo3ta.kam.ui.revamp.PickerSheet
import tools.mo3ta.kam.ui.revamp.KeypadSheet
import tools.mo3ta.kam.ui.revamp.ResultScreen
import tools.mo3ta.kam.ui.revamp.components.KamBottomSheet
import tools.mo3ta.kam.ui.theme.KamColors
import tools.mo3ta.kam.ui.theme.KamTheme
import tools.mo3ta.kam.ui.theme.LocalKamText
import tools.mo3ta.kam.ui.theme.RevampTheme
import tools.mo3ta.kam.viewmodel.KeypadTarget
import tools.mo3ta.kam.viewmodel.OnboardingViewModel
import tools.mo3ta.kam.viewmodel.RevampScreen
import tools.mo3ta.kam.viewmodel.RevampViewModel
import tools.mo3ta.kam.viewmodel.SettingsViewModel

@Composable
fun MainScreen() {
    val repository = remember { CityRepository() }
    val preferenceManager = remember { PreferenceManager() }

    val settingsVM = remember { SettingsViewModel(preferenceManager) }
    val onboardingVM = remember { OnboardingViewModel(preferenceManager) }
    val revampVM = remember { RevampViewModel(repository, preferenceManager) }

    val isDarkMode by settingsVM.isDarkMode.collectAsState()
    val locale by settingsVM.locale.collectAsState()
    var isOnboarded by remember { mutableStateOf(preferenceManager.isOnboarded) }

    // KamTheme provides LocalKamStrings (+ Material theme used by the settings switch).
    KamTheme(darkTheme = isDarkMode, locale = locale) {
        if (!isOnboarded) {
            OnboardingScreen(viewModel = onboardingVM, onComplete = { isOnboarded = true })
        } else {
            RevampTheme {
                RevampApp(revampVM, settingsVM)
            }
        }
    }
}

@Composable
private fun RevampApp(vm: RevampViewModel, settingsVM: SettingsViewModel) {
    val state by vm.uiState.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    val easing = remember { CubicBezierEasing(0.7f, 0f, 0.2f, 1f) }

    Box(
        Modifier
            .fillMaxSize()
            .background(KamColors.paper)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        AnimatedContent(
            targetState = state.screen,
            transitionSpec = {
                val dur = tween<androidx.compose.ui.unit.IntOffset>(420, easing = easing)
                if (targetState == RevampScreen.RESULT) {
                    slideInHorizontally(dur) { it } togetherWith slideOutHorizontally(dur) { -it }
                } else {
                    slideInHorizontally(dur) { -it } togetherWith slideOutHorizontally(dur) { it }
                }
            },
            label = "screen",
        ) { screen ->
            when (screen) {
                RevampScreen.HOME -> HomeScreen(
                    state = state,
                    onOpenPicker = vm::openPicker,
                    onSwap = vm::swap,
                    onEditSalary = { vm.openKeypad(KeypadTarget.SALARY) },
                    onEditOffer = { vm.openKeypad(KeypadTarget.OFFER) },
                    onClearOffer = vm::clearOffer,
                    onCompare = vm::goToResult,
                    onSettings = { showSettings = true },
                )
                RevampScreen.RESULT -> ResultScreen(
                    state = state,
                    onBack = vm::goHome,
                )
            }
        }

        // Overlays — render unconditionally so enter/exit both animate.
        PickerSheet(state = state, onSelect = vm::selectCity, onClose = vm::closePicker)
        KeypadSheet(state = state, onKey = vm::setKeypadValue, onClose = vm::closeKeypad)
        SettingsSheet(
            visible = showSettings,
            settingsVM = settingsVM,
            onClose = { showSettings = false },
        )
    }
}

@Composable
private fun SettingsSheet(
    visible: Boolean,
    settingsVM: SettingsViewModel,
    onClose: () -> Unit,
) {
    val strings = LocalKamStrings.current
    val text = LocalKamText.current
    val isDark by settingsVM.isDarkMode.collectAsState()
    val locale by settingsVM.locale.collectAsState()
    val uriHandler = LocalUriHandler.current

    KamBottomSheet(visible = visible, onDismiss = onClose, title = strings.settingsTitle) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Dark mode
            SettingRow {
                Text(strings.settingsDarkMode, style = text.clrName, color = KamColors.ink, modifier = Modifier.weight(1f))
                Switch(
                    checked = isDark,
                    onCheckedChange = { settingsVM.toggleDarkMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = KamColors.paper,
                        checkedTrackColor = KamColors.green,
                        uncheckedTrackColor = KamColors.card2,
                    ),
                )
            }
            // Language
            SettingRow {
                Text(strings.settingsLanguage, style = text.clrName, color = KamColors.ink, modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LangPill("EN", locale == "en") { settingsVM.setLocale("en") }
                    LangPill("AR", locale == "ar") { settingsVM.setLocale("ar") }
                }
            }
            // Credits
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(KamColors.card2)
                    .padding(16.dp),
            ) {
                Text(strings.settingsCredits, style = text.clrName, color = KamColors.ink)
                Spacer(Modifier.height(8.dp))
                Text(strings.settingsNumbeoDesc, style = text.ocDesc, color = KamColors.ink2)
                Spacer(Modifier.height(12.dp))
                Text(
                    strings.visitNumbeo,
                    style = text.cardBadge,
                    color = KamColors.green,
                    modifier = Modifier.clickable {
                        runCatching { uriHandler.openUri("https://www.numbeo.com/") }
                    },
                )
            }
        }
    }
}

@Composable
private fun SettingRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KamColors.card)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun LangPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val text = LocalKamText.current
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) KamColors.green else KamColors.card2)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = text.cardBadge, color = if (selected) KamColors.paper else KamColors.ink2)
    }
}
