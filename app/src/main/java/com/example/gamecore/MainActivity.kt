package com.example.gamecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.gamecore.ui.theme.GameCoreTheme

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
    var currentScreen by remember {
        mutableStateOf("store")
    }

    var selectedGame by remember {
        mutableStateOf(storeGames.first())
    }

    val cart = remember {
        mutableStateListOf<GameData>()
    }

    val onDestinationClick: (GameCoreDestination) -> Unit = { destination ->
        when (destination) {
            GameCoreDestination.STORE -> currentScreen = "store"
            GameCoreDestination.CART -> currentScreen = "cart"
            GameCoreDestination.LIBRARY -> Unit
            GameCoreDestination.PROFILE -> Unit
        }
    }

    when (currentScreen) {
        "store" -> StoreScreen(
            cartCount = cart.size,
            onOpenGame = { game ->
                selectedGame = game
                currentScreen = "game"
            },
            onOpenCart = {
                currentScreen = "cart"
            },
            onDestinationClick = onDestinationClick
        )

        "game" -> GameScreen(
            game = selectedGame,
            cartCount = cart.size,
            onBack = {
                currentScreen = "store"
            },
            onOpenCart = {
                currentScreen = "cart"
            },
            onAddToCart = {
                if (cart.contains(selectedGame)) {
                    false
                } else {
                    cart.add(selectedGame)
                    true
                }
            },
            onDestinationClick = onDestinationClick
        )

        "cart" -> CartScreen(
            cart = cart,
            onRemoveItem = { game ->
                cart.remove(game)
            },
            onContinueShopping = {
                currentScreen = "store"
            },
            onDestinationClick = onDestinationClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameCorePreview() {
    GameCoreTheme {
        StoreScreen()
    }
}
