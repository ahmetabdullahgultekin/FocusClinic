package com.focusclinic.app.presentation.screens.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.focusclinic.app.presentation.Strings
import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.model.ModifierType
import com.focusclinic.domain.model.ShopItem

@Composable
fun ShopScreen(viewModel: ShopViewModel) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.purchaseSuccessMessage) {
        state.purchaseSuccessMessage?.let { name ->
            snackbarHostState.showSnackbar("$name — ${Strings.SHOP_PURCHASED}")
            viewModel.onIntent(ShopIntent.DismissPurchaseSuccess)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onIntent(ShopIntent.DismissError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.selectedTab == ShopTab.CUSTOM_REWARDS) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = Strings.SHOP_CREATE_REWARD,
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            BalanceHeader(balance = state.balance.amount)

            @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
            PrimaryTabRow(
                selectedTabIndex = state.selectedTab.ordinal,
            ) {
                ShopTab.entries.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.onIntent(ShopIntent.SelectTab(tab)) },
                        text = {
                            Text(
                                when (tab) {
                                    ShopTab.VIRTUAL_SHOP -> Strings.SHOP_TAB_VIRTUAL
                                    ShopTab.CUSTOM_REWARDS -> Strings.SHOP_TAB_REWARDS
                                },
                            )
                        },
                    )
                }
            }

            when (state.selectedTab) {
                ShopTab.VIRTUAL_SHOP -> VirtualShopContent(
                    items = state.shopItems,
                    ownedItemIds = state.ownedItemIds,
                    balance = state.balance.amount,
                    onPurchase = { viewModel.onIntent(ShopIntent.PurchaseItem(it)) },
                )
                ShopTab.CUSTOM_REWARDS -> CustomRewardsContent(
                    rewards = state.customRewards,
                    balance = state.balance.amount,
                    onRedeem = { viewModel.onIntent(ShopIntent.PurchaseReward(it)) },
                    onDelete = { viewModel.onIntent(ShopIntent.DeleteReward(it)) },
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateRewardDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, cost ->
                viewModel.onIntent(ShopIntent.CreateReward(title, cost))
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun BalanceHeader(balance: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "\uD83E\uDDB7",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${Strings.FOCUS_BALANCE}: $balance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun VirtualShopContent(
    items: List<ShopItem>,
    ownedItemIds: Set<String>,
    balance: Long,
    onPurchase: (ShopItem) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            ShopItemCard(
                item = item,
                isOwned = item.id in ownedItemIds,
                canAfford = balance >= item.cost.amount,
                onPurchase = { onPurchase(item) },
            )
        }
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    isOwned: Boolean,
    canAfford: Boolean,
    onPurchase: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = itemEmoji(item),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = modifierLabel(item.modifier),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "\uD83E\uDDB7 ${item.cost.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (isOwned) {
                Text(
                    text = Strings.SHOP_OWNED,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = canAfford,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Strings.SHOP_BUY)
                }
            }
        }
    }
}

@Composable
private fun CustomRewardsContent(
    rewards: List<CustomReward>,
    balance: Long,
    onRedeem: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    if (rewards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = Strings.SHOP_NO_REWARDS,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(rewards, key = { it.id }) { reward ->
                RewardCard(
                    reward = reward,
                    canAfford = balance >= reward.cost.amount,
                    onRedeem = { onRedeem(reward.id) },
                    onDelete = { onDelete(reward.id) },
                )
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: CustomReward,
    canAfford: Boolean,
    onRedeem: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\uD83C\uDF81",  // gift emoji
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "\uD83E\uDDB7 ${reward.cost.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            OutlinedButton(
                onClick = onRedeem,
                enabled = canAfford,
            ) {
                Text(Strings.SHOP_REDEEM)
            }

            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = Strings.SHOP_DELETE_CONFIRM,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(Strings.SHOP_DELETE_CONFIRM) },
            text = { Text(reward.title) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text(Strings.SHOP_YES, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(Strings.SHOP_NO)
                }
            },
        )
    }
}

@Composable
private fun CreateRewardDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Long) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.SHOP_CREATE_REWARD) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(Strings.SHOP_REWARD_TITLE) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it.filter { c -> c.isDigit() } },
                    label = { Text(Strings.SHOP_REWARD_COST) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cost = costText.toLongOrNull()
                    if (title.isNotBlank() && cost != null && cost > 0) {
                        onCreate(title.trim(), cost)
                    }
                },
                enabled = title.isNotBlank() && (costText.toLongOrNull() ?: 0) > 0,
            ) {
                Text(Strings.SHOP_SAVE)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.SHOP_CANCEL)
            }
        },
    )
}

private fun itemEmoji(item: ShopItem): String = when (item.id) {
    "ergonomic_chair" -> "\uD83E\uDE91"     // chair
    "led_lamp" -> "\uD83D\uDCA1"            // light bulb
    "sterilizer_pro" -> "\uD83E\uDDF4"      // sponge (sterilizer proxy)
    "digital_xray" -> "\uD83E\uDE7B"        // x-ray
    "wall_paint_ocean" -> "\uD83C\uDF0A"     // wave
    "diploma_frame" -> "\uD83C\uDF93"        // graduation cap
    "potted_plant" -> "\uD83C\uDF3F"         // herb
    "aquarium" -> "\uD83D\uDC20"             // tropical fish
    else -> "\uD83D\uDCE6"                   // package
}

private fun modifierLabel(modifier: ModifierType): String = when (modifier) {
    is ModifierType.XpBonus -> "+${(modifier.bonusValue * 100).toInt()}% ${Strings.SHOP_XP_BONUS}"
    is ModifierType.CoinBonus -> "+${(modifier.bonusValue * 100).toInt()}% ${Strings.SHOP_COIN_BONUS}"
    ModifierType.None -> Strings.SHOP_DECORATION
}
