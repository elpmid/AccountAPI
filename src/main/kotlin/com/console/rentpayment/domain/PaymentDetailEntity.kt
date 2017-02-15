/*
 * Copyright 2016 Onthehouse Holdings Pty Ltd. All rights reserved. Not to be copied, redistributed
 * or modified without prior written consent of Onthehouse Holdings Pty Ltd.
 */

package com.console.rentpayment.domain

import com.console.rentpayment.domain.PaymentEntity
import javax.persistence.*

/**
 * @author Alan Loi (alan.loi@console.com.au)
 */
@Entity
@Table(name = "vw_payment_detail")
data class PaymentDetailEntity(

        @Id
        @Column(name = "payment_id")
        val paymentId: Long? = null,

        // CHEQUE & EFT types
        @Column(name = "payee")
        val payeeName: String? = null,

        @Column(name = "reconciliation_id")
        var reconciliationId: Long? = null,

        //
        // CHEQUE type only
        //

        @Column(name = "cheque_number")
        val chequeNumber: Long? = null,

        //
        // EFT type only
        //

        @Column(name = "processed")
        var processed: Boolean? = null,

        @Column(name = "bank_name")
        val bankName: String? = null,

        @Column(name = "bank_account_name")
        val bankAccountName: String? = null,

        @Column(name = "bsb_number")
        val bsbNumber: String? = null,

        @Column(name = "bank_account_number")
        val bankAccountNumber: String? = null,

        //
        // JOURNAL type only
        //

        @Column(name = "creditor_id")
        val creditorId: Long? = null
) {
        @MapsId
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "payment_id")
        lateinit var paymentEntity: PaymentEntity
}
