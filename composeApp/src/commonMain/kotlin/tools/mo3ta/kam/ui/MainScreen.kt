package tools.mo3ta.kam.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import tools.mo3ta.kam.analytics.logScreenView
import tools.mo3ta.kam.analytics.logTabSwitch
import tools.mo3ta.kam.data.CityRepository
import tools.mo3ta.kam.viewmodel.CityListViewModel
import tools.mo3ta.kam.viewmodel.CompareViewModel
import tools.mo3ta.kam.viewmodel.ConvertViewModel

private data class TabItem(val label: String, val emoji: String, val index: Int)

@Composable
fun MainScreen() {
    val repository = remember { CityRepository() }
    val cityListVM = remember { CityListViewModel(repository) }
    val convertVM = remember { ConvertViewModel(repository) }
    val compareVM = remember { CompareViewModel(repository) }

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        TabItem("Cities", "🏙️", 0),
        TabItem("Convert", "💱", 1),
        TabItem("Compare", "⚖️", 2)
    )

    // Log tab switches and screen views
    LaunchedEffect(selectedTab) {
        val tabName = tabs[selectedTab].label
        logTabSwitch(tabName)
        logScreenView(tabName)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 16.dp,
                modifier = Modifier.shadow(8.dp)
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab.index,
                        onClick = { selectedTab = tab.index },
                        icon = {
                            Text(
                                text = tab.emoji,
                                fontSize = 20.sp
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontWeight = if (selectedTab == tab.index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally { it * direction } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it * direction } + fadeOut())
                }
            ) { tab ->
                when (tab) {
                    0 -> CityListScreen(viewModel = cityListVM)
                    1 -> ConvertScreen(viewModel = convertVM)
                    2 -> CompareScreen(viewModel = compareVM)
                }
            }
        }
    }
}
