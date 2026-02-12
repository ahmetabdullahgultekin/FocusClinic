package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ShopItem
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.TransactionType
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.repository.TransactionRepository

class PurchaseShopItemUseCase(
    private val inventoryRepository: InventoryRepository,
    private val transactionRepository: TransactionRepository,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(item: ShopItem): DomainResult<InventoryItem> {
        if (inventoryRepository.exists(item.id)) {
            return DomainResult.Failure(DomainError.PurchaseError.ItemAlreadyOwned(item.id))
        }

        val balance = transactionRepository.getBalance()
        if (balance.amount < item.cost.amount) {
            return DomainResult.Failure(
                DomainError.PurchaseError.InsufficientCoins(
                    required = item.cost.amount,
                    available = balance.amount,
                )
            )
        }

        val now = clock()

        transactionRepository.record(
            Transaction(
                id = idGenerator(),
                type = TransactionType.SpendShop,
                amount = -item.cost.amount,
                referenceId = item.id,
                description = "Purchased ${item.name}",
                createdAt = now,
            )
        )

        val inventoryItem = InventoryItem(
            itemId = item.id,
            name = item.name,
            type = item.type,
            modifier = item.modifier,
            purchasedAt = now,
        )
        inventoryRepository.add(inventoryItem)

        return DomainResult.Success(inventoryItem)
    }
}
