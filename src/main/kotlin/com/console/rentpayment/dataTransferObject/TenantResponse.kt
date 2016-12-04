package com.console.rentpayment.dataTransferObject

import org.joda.money.Money
import java.time.LocalDateTime

/**
 * Created by Nick on 14/11/2016.
 */
class TenantResponse(id : Long? , val name : String,  val weeklyRentAmount : Money,  val rentDatePaidTo : LocalDateTime, val rentCreditAmount : Money,
                     createdBy : String, createdDate : LocalDateTime, lastModifiedBy : String, lastModifiedDate : LocalDateTime) :
                     AbstractResponse( id, createdDate, lastModifiedDate,  createdBy, lastModifiedBy) {
    override fun toString(): String {
        return "TenantResponse(name='$name', weeklyRentAmount=$weeklyRentAmount, rentDatePaidTo=$rentDatePaidTo, rentCreditAmount=$rentCreditAmount)"
    }
}