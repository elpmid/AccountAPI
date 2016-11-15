package com.console.rentpayment.dataTransferObject

import org.joda.money.Money
import java.time.LocalDateTime

/**
 * Created by Nick on 14/11/2016.
 */
class RentReceiptResponse(id : Long?, val tenantId : Long?, val amount : Money, createdDate : LocalDateTime, lastModifiedDate : LocalDateTime,
                          createdBy: String, lastModifiedBy: String) : AbstractResponse(id, createdDate, lastModifiedDate, createdBy, lastModifiedBy) {



}