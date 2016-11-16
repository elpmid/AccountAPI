package com.console.rentpayment.domain

import com.example.domain.AbstractEntity
import org.joda.money.Money
import javax.persistence.Column
import javax.persistence.Entity

/**
 * Created by Nick on 14/11/2016.
 */

@Entity(name = "RENT_RECEIPT")
open class RentReceipt() : AbstractEntity() {
    constructor(amount: Money) : this() {
        this.amount = amount
    }

    @Column(name = "AMT", nullable = false)
    lateinit var amount : Money


}