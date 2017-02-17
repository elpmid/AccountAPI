package com.console.rentpayment.domain

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


/**
 * Created by Nick on 14/11/2016.
 */

@Entity(name = "TENANT")
@NamedEntityGraph(name = "Tenant.tenantRentReceipts",
                  attributeNodes = arrayOf(NamedAttributeNode(value = "rentReceipts")))
open class Tenant() : AbstractEntity() {
    constructor(name: String, weeklyRentAmount: Money, rentDatePaidTo : LocalDateTime = LocalDateTime.now(), rentCreditAmount : Money =  Money.of(CurrencyUnit.of("AUD"), BigDecimal(0))) : this() {
        this.name = name
        this.weeklyRentAmount = weeklyRentAmount
        this.rentDatePaidTo = rentDatePaidTo
        this.rentCreditAmount = rentCreditAmount
    }

    @Column(name = "NAME", unique = true, nullable=false)
    lateinit var name : String

    @Column(name = "WKLY_RENT_AMT", nullable=false)
    lateinit var  weeklyRentAmount: Money

    @Column(name = "RENT_DTE_PAID_TO", nullable=false)
    lateinit var rentDatePaidTo : LocalDateTime

    @Column(name = "RENT_CREDIT_AMT", nullable=false)
    lateinit var rentCreditAmount : Money

    @OneToMany(fetch=FetchType.LAZY, cascade=arrayOf(CascadeType.ALL))
    @JoinColumn(name="TENANT_ID", nullable=true)
    var rentReceipts: MutableList<RentReceipt> = ArrayList()

    override fun toString(): String {
        return "Tenant(name='$name', weeklyRentAmount=$weeklyRentAmount, rentDatePaidTo=$rentDatePaidTo, rentCreditAmount=$rentCreditAmount, rentReceipts=$rentReceipts)"
    }


}