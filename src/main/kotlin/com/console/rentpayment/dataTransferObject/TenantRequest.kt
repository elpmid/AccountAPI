package com.console.rentpayment.dataTransferObject

import org.joda.money.Money
import java.time.LocalDateTime

/**
 * Created by Nick on 14/11/2016.
 */
class TenantRequest() {

    constructor(name: String, weeklyRentAmount: Money, rentDatePaidTo : LocalDateTime, rentCreditAmount : Money) : this() {
        this.name = name
        this.weeklyRentAmount = weeklyRentAmount
        this.rentDatePaidTo = rentDatePaidTo
        this.rentCreditAmount = rentCreditAmount
    }

    lateinit  var name: String
    lateinit  var weeklyRentAmount: Money
    lateinit  var rentDatePaidTo : LocalDateTime
    lateinit  var rentCreditAmount : Money

    override fun toString(): String {
        return "TenantRequest(name='$name', weeklyRentAmount=$weeklyRentAmount, rentDatePaidTo=$rentDatePaidTo, rentCreditAmount=$rentCreditAmount)"
    }


}