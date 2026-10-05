package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.model.EmailMessage
import com.example.ui.screens.*
import com.example.viewmodel.MailvoraViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
  object Splash : Screen("splash", "Splash", Icons.Default.Star)
  object Onboarding : Screen("onboarding", "Onboarding", Icons.Default.Star)
  object Inbox : Screen("inbox", "Inbox", Icons.Default.Inbox)
  object Generate : Screen("generate", "New Mail", Icons.Default.AddCircle)
  object History : Screen("history", "History", Icons.Default.History)
  object Settings : Screen("settings", "Settings", Icons.Default.Settings)
  object Reader : Screen("reader/{messageId}", "Reader", Icons.Default.Email) {
    fun createRoute(messageId: String) = "reader/$messageId"
  }
  object Privacy : Screen("privacy", "Privacy", Icons.Default.Security)
}

@Composable
fun MailvoraNavGraph(viewModel: MailvoraViewModel) {
  val navController = rememberNavController()
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  var currentSelectedMessage by remember { mutableStateOf<EmailMessage?>(null) }

  val bottomNavScreens = listOf(
    Screen.Inbox,
    Screen.Generate,
    Screen.History,
    Screen.Settings
  )

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  Scaffold(
    bottomBar = {
      if (currentRoute != Screen.Splash.route && currentRoute != Screen.Onboarding.route && currentRoute != Screen.Reader.route && currentRoute != Screen.Privacy.route) {
        NavigationBar {
          bottomNavScreens.forEach { screen ->
            NavigationBarItem(
              icon = { Icon(screen.icon, contentDescription = screen.title) },
              label = { Text(screen.title) },
              selected = currentRoute == screen.route,
              onClick = {
                if (currentRoute != screen.route) {
                  navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              modifier = Modifier.testTag("nav_${screen.route}")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Splash.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      composable(Screen.Splash.route) {
        SplashAuthScreen(
          onSplashFinished = {
            navController.navigate(Screen.Onboarding.route) {
              popUpTo(Screen.Splash.route) { inclusive = true }
            }
          }
        )
      }
      composable(Screen.Onboarding.route) {
        OnboardingScreen(
          onFinished = {
            navController.navigate(Screen.Inbox.route) {
              popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
          }
        )
      }
      composable(Screen.Inbox.route) {
        HomeScreen(
          state = state,
          onRefresh = { viewModel.refreshMessages() },
          onDeleteMailbox = { viewModel.deleteCurrentMailbox() },
          onOpenMessage = { msg ->
            currentSelectedMessage = msg
            navController.navigate(Screen.Reader.createRoute(msg.id))
          },
          onSearchChanged = { viewModel.setSearchQuery(it) },
          onFilterChanged = { viewModel.setFilterType(it) },
          filteredMessages = viewModel.filteredMessages
        )
      }
      composable(Screen.Generate.route) {
        GenerateMailScreen(
          state = state,
          onCreateCustomMailbox = { user, domain -> viewModel.createMailbox(user, domain) },
          onCreateRandomMailbox = { viewModel.createMailbox() },
          onNavigateToInbox = {
            navController.navigate(Screen.Inbox.route) {
              popUpTo(Screen.Inbox.route) { inclusive = true }
            }
          }
        )
      }
      composable(Screen.History.route) {
        HistoryScreen(
          state = state,
          onSelectMailbox = { viewModel.selectMailbox(it) },
          onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
          onClearHistory = { viewModel.clearHistory() },
          onNavigateToInbox = {
            navController.navigate(Screen.Inbox.route) {
              popUpTo(Screen.Inbox.route) { inclusive = true }
            }
          }
        )
      }
      composable(Screen.Settings.route) {
        SettingsScreen(
          state = state,
          onUpdateSettings = { dark, ref, notif, base ->
            viewModel.updateSettings(dark, ref, notif, base)
          },
          onNavigateToPrivacy = {
            navController.navigate(Screen.Privacy.route)
          }
        )
      }
      composable(
        route = Screen.Reader.route,
        arguments = listOf(navArgument("messageId") { type = NavType.StringType })
      ) { backStackEntry ->
        EmailReaderScreen(
          message = currentSelectedMessage,
          onBack = { navController.popBackStack() },
          onDelete = { msgId ->
            state.currentMailbox?.let { box ->
              // delete message logic
            }
          }
        )
      }
      composable(Screen.Privacy.route) {
        PrivacyCenterScreen(onBack = { navController.popBackStack() })
      }
    }
  }
}
