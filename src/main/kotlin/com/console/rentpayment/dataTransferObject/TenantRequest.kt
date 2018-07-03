package com.console.rentpayment.dataTransferObject

import org.joda.money.Money
import java.time.LocalDateTime

class TenantRequest(
        val leaseName: String,
        val name: String,
        val weeklyRentAmount: Money,
        val rentDatePaidTo: LocalDateTime,
        val rentCreditAmount: Money
)