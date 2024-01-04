package com.example.hokiemoneymanager

import java.math.BigDecimal

data class Currency(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Map<String, BigDecimal>
)

