/*
 * Copyright 2016 Onthehouse Holdings Pty Ltd. All rights reserved. Not to be copied, redistributed
 * or modified without prior written consent of Onthehouse Holdings Pty Ltd.
 */

package com.console.rentpayment.domain

import javax.persistence.*

/**
 * @author Alan Loi (alan.loi@console.com.au)
 */
@Entity
@Table(name = "vw_payment_item")
data class PaymentItemEntity(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "payment_item_id")
        val paymentItemId: Long? = null,

        @Column(name = "payment_id", insertable = false, updatable = false)
        val paymentId: Long? = null,

        @Column(name = "invoice_number")
        val invoiceNumber: String? = null,

        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
        @JoinTable(name = "vw_payment_item_transaction", joinColumns = arrayOf(JoinColumn(name = "payment_item_id")),
                inverseJoinColumns = arrayOf(JoinColumn(
                        name = "transaction_id")))
        val transactionEntities: MutableSet<TransactionEntity>
) {

    /**
     * Default constructor for Hibernate.
     */
    private constructor() : this(
            transactionEntities = mutableSetOf<TransactionEntity>()
    )
}
