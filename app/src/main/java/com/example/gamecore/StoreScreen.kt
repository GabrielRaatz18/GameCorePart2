package com.example.gamecore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamecore.ui.theme.GameCoreTheme

@Composable
fun StoreScreen(
    cartCount: Int = 0,
    onOpenGame: (GameData) -> Unit = {},
    onOpenCart: () -> Unit = {},
    onDestinationClick: (GameCoreDestination) -> Unit = {}
) {
    var selectedCategory by remember {
        mutableStateOf("Destaques")
    }

    val categories = listOf(
        "Destaques",
        "Ação",
        "RPG",
        "Corrida",
        "Indie",
        "Multiplayer",
        "Terror"
    )

    GameCorePage(
        selectedDestination = GameCoreDestination.STORE,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "GameCore",
                subtitle = "Encontre seu próximo jogo",
                cartCount = cartCount,
                onCartClick = onOpenCart
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GameCoreDimens.ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            GameCoreSearchBar()

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    GameCoreCategoryChip(
                        text = category,
                        selected = category == selectedCategory,
                        onClick = {
                            selectedCategory = category
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

            StoreHeroCard(
                game = storeGames[0],
                onClick = {
                    onOpenGame(storeGames[0])
                }
            )

            Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

            GameCoreSectionHeader(
                title = "Ofertas para você",
                action = "Ver todas"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                storeGames
                    .filter { it.discount != null }
                    .forEach { game ->
                        StoreOfferCard(
                            game = game,
                            onClick = {
                                onOpenGame(game)
                            }
                        )
                    }
            }

            Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

            GameCoreSectionHeader(title = "Mais jogados")

            Spacer(modifier = Modifier.height(10.dp))

            storeGames.drop(4).forEach { game ->
                StorePopularGameCard(
                    game = game,
                    onClick = {
                        onOpenGame(game)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StoreHeroCard(
    game: GameData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GameCoreColors.Card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GameCoreColors.Border
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GameCoreImage(
                imageRes = game.imageRes,
                modifier = Modifier.fillMaxSize(),
                label = "BANNER DO JOGO",
                cornerRadius = 16
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(heroOverlayBrush())
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = game.name,
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = game.category,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    GameCorePrice(
                        price = game.price.toBrazilianPrice(),
                        fontSize = 19
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    GameCorePrimaryButton(
                        text = "Ver jogo",
                        modifier = Modifier.width(116.dp),
                        onClick = onClick
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (index == 0) 18.dp else 7.dp)
                            .height(7.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (index == 0) {
                                    GameCoreColors.Orange
                                } else {
                                    GameCoreColors.NavInactive
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreOfferCard(
    game: GameData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(184.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = GameCoreColors.Card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GameCoreColors.Border
        )
    ) {
        Column {
            Box {
                GameCoreImage(
                    imageRes = game.imageRes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    label = "CAPA",
                    cornerRadius = 14
                )

                game.discount?.let { discount ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = GameCoreColors.Orange,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = discount,
                            color = Color.Black,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 5.dp
                            )
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = game.name,
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

                game.oldPrice?.let { oldPrice ->
                    Text(
                        text = oldPrice.toBrazilianPrice(),
                        color = GameCoreColors.TextDisabled,
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                GameCorePrice(
                    price = game.price.toBrazilianPrice(),
                    color = GameCoreColors.Orange,
                    fontSize = 17
                )
            }
        }
    }
}

@Composable
private fun StorePopularGameCard(
    game: GameData,
    onClick: () -> Unit
) {
    GameCoreCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameCoreImage(
                imageRes = game.imageRes,
                modifier = Modifier.size(
                    width = 100.dp,
                    height = 72.dp
                ),
                label = "CAPA",
                cornerRadius = 10
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = game.category,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(7.dp))

                GameCorePrice(
                    price = game.price.toBrazilianPrice(),
                    color = GameCoreColors.Orange,
                    fontSize = 16
                )
            }

            Surface(
                modifier = Modifier.size(36.dp),
                color = GameCoreColors.CardElevated,
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_favorite),
                        contentDescription = "Favoritar",
                        tint = GameCoreColors.TextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun StorePreview() {
    GameCoreTheme {
        StoreScreen()
    }
}
