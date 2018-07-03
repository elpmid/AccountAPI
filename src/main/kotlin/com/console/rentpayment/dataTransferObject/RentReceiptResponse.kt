package com.console.rentpayment.dataTransferObject

import org.joda.money.Money
import java.time.LocalDateTime

class RentReceiptResponse(
        id : Long,
        val tenantId : Long,
        val amount : Money,
        createdDate : LocalDateTime,
        lastModifiedDate : LocalDateTime,
        createdBy: String,
        lastModifiedBy: String
) : ResponseBase(id, createdDate, lastModifiedDate, createdBy, lastModifiedBy)