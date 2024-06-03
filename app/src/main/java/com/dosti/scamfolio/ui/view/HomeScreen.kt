package com.dosti.scamfolio.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dosti.scamfolio.R
import com.dosti.scamfolio.ui.theme.ScamFolioTheme
import com.dosti.scamfolio.ui.theme.custom
import com.dosti.scamfolio.ui.view.ScreenRouter.Companion.COIN_DETAIL
import com.dosti.scamfolio.viewModel.ViewModelFactory
@Composable
fun ComposeCryptoPages(factory : ViewModelFactory, viewModelStoreOwner: ViewModelStoreOwner) {
    val navController = rememberNavController()

    ScamFolioTheme {
        Scaffold(
            topBar = {
                ComposeCryptoTopBar()
            },
            bottomBar = {
                ComposeCryptoBottomBar(navController)
            }
        ) {
            ComposeCryptoNavHost(
                navController = navController,
                factory = factory,
                modifier = Modifier.padding(it),
                viewModelStoreOwner
            )
        }
    }
}

@SuppressLint("ResourceAsColor")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeCryptoTopBar() {

    TopAppBar(
        title = { Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = custom,
            color = Color.White
            ) },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xD40A0A0A))
    )
}

@Composable
fun ComposeCryptoBottomBar(navController: NavHostController) {
    BottomAppBar(
        containerColor = Color(0xD40A0A0A)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        ScreenRouter.screens.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                          navController.navigate(screen.route) {
                              launchSingleTop = true
                          }
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        color = Color.White
                    )
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(id = screen.label),
                        tint = Color.White
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF4BC096)
                )
            )
        }
    }
}

@Composable
fun ComposeCryptoNavHost(
    navController: NavHostController,
    factory: ViewModelProvider.Factory?,
    modifier: Modifier,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val actions = remember(navController) { AppActions(navController) }

    NavHost(
        navController = navController,
        startDestination = ScreenRouter.ROUTE_PERSONALAREA,
        modifier = modifier
    ) {
        composable(ScreenRouter.ROUTE_PERSONALAREA) {
            Welcome(viewModelStoreOwner = viewModelStoreOwner, homepageViewModel = viewModel(factory = factory))
        }

        composable(ScreenRouter.ROUTE_SEARCHCOIN) {
            SearchedCryptos(viewModelStoreOwner = viewModelStoreOwner, viewModel = viewModel(factory = factory), selectedCoin = actions.selectedCoin)
        }
        composable(
            "${ScreenRouter.ROUTE_SEARCHCOIN}/{$COIN_DETAIL}",
            arguments = listOf(
                navArgument(COIN_DETAIL) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            arguments.getString(COIN_DETAIL)?.let {
                CryptoScreen(
                    viewModel = viewModel(factory = factory),
                    coinName = it,
                    navigateUp = actions.navigateUp
                )
            }
        }
    }
}
class AppActions(
    navController: NavHostController
) {
    val selectedCoin: (String) -> Unit = { coinName: String ->
        navController.navigate("${ScreenRouter.ROUTE_SEARCHCOIN}/$coinName")
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}
