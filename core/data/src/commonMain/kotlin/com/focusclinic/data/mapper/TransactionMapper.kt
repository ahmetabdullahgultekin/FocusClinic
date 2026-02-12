package com.focusclinic.data.mapper

import com.focusclinic.data.database.Transactions
import com.focusclinic.domain.model.Transaction

fun Transactions.toDomain(): Transaction = Transaction(
    id = id,
    type = type.toTransactionType(),
    amount = amount,
    referenceId = reference_id,
    description = description,
    createdAt = created_at,
)
