package com.console.rentpayment.dataTransferObject

import org.joda.money.Money
import java.time.LocalDateTime

class TenantResponse(
        id: Long,
        val leaseName: String,
        val name: String,
        val weeklyRentAmount: Money,
        val rentDatePaidTo: LocalDateTime,
        val rentCreditAmount: Money,
        createdBy: String,
        createdDate: LocalDateTime,
        lastModifiedBy: String,
        lastModifiedDate: LocalDateTime
) : ResponseBase(id, createdDate, lastModifiedDate, createdBy, lastModifiedBy)