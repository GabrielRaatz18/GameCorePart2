package com.example.gamecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamecore.ui.theme.GameCoreTheme

object Routes {
    const val Store = "store"
    const val Cart = "cart"
    const val Library = "library"
    const val Profile = "profile"
    const val Settings = "settings"
    const val Wishlist = "wishlist"
    const val Reviews = "reviews/{gameId}"
    const val Game = "game/{gameId}"
    const val Review = "review/{reviewId}"

    fun game(id: String) = "game/$id"
    fun reviews(gameId: String) = "reviews/$gameId"
    fun review(id: String) = "review/$id"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameCoreTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val cart = remember { mutableStateListOf<GameData>() }
    val library = remember { mutableStateListOf(storeGames[0], storeGames[3], storeGames[4]) }
    val wishlist = remember { mutableStateListOf(storeGames[1], storeGames[2], storeGames[5]) }
    val reviews = remember {
        mutableStateListOf(
            ReviewData("gabriel", storeGames[0].id, "Gabriel S.", 5, "Obra de arte absoluta. O mundo aberto é um dos mais recompensadores que já joguei.", 12),
            ReviewData("mariana", storeGames[0].id, "Mariana K.", 5, "Design de arte espetacular. A sensação de progresso é incrível.", 8),
            ReviewData("arthur", storeGames[0].id, "Arthur M.", 5, "O combate refinado torna cada encontro especial.", 4),
            ReviewData("lucas", storeGames[1].id, "Lucas R.", 5, "História incrível e mundo vivo inigualável.", 15),
            ReviewData("pedro", storeGames[3].id, "Pedro H.", 5, "Uma jornada emocionante e inesquecível.", 9),
            ReviewData("rafael", storeGames[4].id, "Rafael C.", 5, "Ansiedade a mil! Gráficos incríveis no trailer.", 20)
        )
    }
    val destinations: (GameCoreDestination) -> Unit = { navController.navigateTopLevel(it.route()) }
    val allGames: () -> List<GameData> = { (storeGames + library + wishlist).distinctBy { it.id } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GameCoreColors.Background
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.Store,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(Routes.Store) {
                StoreScreen(
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onOpenCart = { destinations(GameCoreDestination.CART) },
                    onDestinationClick = destinations,
                    onOpenProfile = { destinations(GameCoreDestination.PROFILE) }
                )
            }
            composable(Routes.Cart) {
                CartScreen(
                    cart = cart,
                    onRemoveItem = { cart.remove(it) },
                    onContinueShopping = { destinations(GameCoreDestination.STORE) },
                    onCheckout = {
                        cart.filterNot { bought -> library.any { it.id == bought.id } }.forEach { library.add(it) }
                        cart.clear()
                        destinations(GameCoreDestination.LIBRARY)
                    },
                    onDestinationClick = destinations,
                    onOpenProfile = { destinations(GameCoreDestination.PROFILE) }
                )
            }
            composable(Routes.Library) {
                LibraryScreen(
                    games = library,
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onDestinationClick = destinations,
                    onProfile = { destinations(GameCoreDestination.PROFILE) }
                )
            }
            composable(Routes.Profile) {
                ProfileScreen(
                    library = library,
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onOpenLibrary = { destinations(GameCoreDestination.LIBRARY) },
                    onOpenSettings = { navController.navigate(Routes.Settings) },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Settings) {
                SettingsScreen(
                    cartCount = cart.size,
                    gamesCount = library.size,
                    wishlistCount = wishlist.size,
                    onBack = { navController.popBackStack() },
                    onOpenWishlist = { navController.navigate(Routes.Wishlist) },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Wishlist) {
                WishlistScreen(
                    games = wishlist,
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onRemove = { wishlist.remove(it) },
                    onBack = { navController.popBackStack() },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Reviews) { entry ->
                val gameId = entry.arguments?.getString("gameId") ?: storeGames.first().id
                val game = allGames().firstOrNull { it.id == gameId } ?: storeGames.first()
                val gameReviews = reviews.filter { it.gameId == game.id }
                ReviewsScreen(
                    game = game,
                    reviews = gameReviews,
                    cartCount = cart.size,
                    onOpen = { navController.navigate(Routes.review(it.id)) },
                    onRemove = { reviews.remove(it) },
                    onAdd = { reviews.add(it) },
                    onBack = { navController.popBackStack() },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Game) { entry ->
                val game = allGames().firstOrNull { it.id == entry.arguments?.getString("gameId") } ?: storeGames.first()
                GameScreen(
                    game = game,
                    cartCount = cart.size,
                    onBack = { navController.popBackStack() },
                    onOpenCart = { destinations(GameCoreDestination.CART) },
                    onAddToCart = { if (cart.contains(game)) false else { cart.add(game); true } },
                    onAddToWishlist = { if (wishlist.contains(game)) false else { wishlist.add(game); true } },
                    onOpenReviews = { navController.navigate(Routes.reviews(game.id)) },
                    isOwned = library.any { it.id == game.id },
                    reviews = reviews,
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Review) { entry ->
                reviews.firstOrNull { it.id == entry.arguments?.getString("reviewId") }?.let { review ->
                    ReviewDetailScreen(
                        review = review,
                        cartCount = cart.size,
                        onSave = { updated -> reviews[reviews.indexOfFirst { it.id == updated.id }] = updated },
                        onBack = { navController.popBackStack() },
                        onDestinationClick = destinations
                    )
                }
            }
        }
    }
}

private fun GameCoreDestination.route() = when (this) {
    GameCoreDestination.STORE -> Routes.Store
    GameCoreDestination.LIBRARY -> Routes.Library
    GameCoreDestination.CART -> Routes.Cart
    GameCoreDestination.PROFILE -> Routes.Profile
}

private fun NavHostController.navigateTopLevel(route: String) {
    if (currentDestination?.route != route) {
        val popped = popBackStack(route, inclusive = false)
        if (!popped) {
            navigate(route) {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
