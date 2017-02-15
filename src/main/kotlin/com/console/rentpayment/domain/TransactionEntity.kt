/*
 * Copyright 2016 Onthehouse Holdings Pty Ltd. All rights reserved. Not to be copied, redistributed
 * or modified without prior written consent of Onthehouse Holdings Pty Ltd.
 */

package com.console.rentpayment.domain

import jdk.nashorn.internal.ir.annotations.Immutable
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY


/**
 * Created by asafamit on 5/04/2016.
 */
@Entity
@Table(name = "vw_transaction")
@Immutable
data class TransactionEntity(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "transaction_id", insertable = false)
        val transactionId: Long? = null,

        @Column(name = "transaction_category")
        @Enumerated(EnumType.STRING)
        val transactionCategory: TransactionCategory,

        @Column(name = "transaction_type")
        @Enumerated(EnumType.STRING)
        val transactionType: TransactionType,

        @Column(name = "transaction_date_local")
        val transactionDate: LocalDate,

        @OneToOne(cascade = arrayOf(CascadeType.ALL), fetch = LAZY)
        @PrimaryKeyJoinColumn
        val transactionDetailEntity: TransactionDetailEntity,

        @Column(name = "audit_reference", insertable = false)
        val auditReference: String? = null,

        @Column(name = "reference")
        val reference: String,

        @Column(name = "amount")
        val amount: Long,

        @Column(name = "gst_amount")
        val gstAmount: Long,

        @Column(name = "ledger_holder_id")
        val ledgerHolderId: Long,

        @Column(name = "ledger_type")
        @Enumerated(javax.persistence.EnumType.STRING)
        val ledgerType: LedgerType,

        @Column(name = "ledger_record_type")
        @Enumerated(javax.persistence.EnumType.STRING)
        val ledgerRecordType: LedgerRecordType
) {


        private constructor() : this(
                transactionCategory = TransactionCategory.PAYMENT,
                transactionType = TransactionType.ADVERTISING,
                transactionDate = LocalDate.MIN,
                amount = 0,
                gstAmount = 0,
                auditReference = "",
                reference = "",
                ledgerType = LedgerType.BANK,
                ledgerRecordType = LedgerRecordType.CREDIT,
                ledgerHolderId = 0,
                transactionDetailEntity = TransactionDetailEntity(sourceReference = "sref", sourceDescription = "sdesc",
                        createdByUserId = 1L, details = "det", transactionMethods = "AA")
        )
}
enum class TransactionType(val description: String) {
        ADVERTISING("Advertising"),
        BANK("Bank"),
        BOND("Bond"),
        CREDITOR("Creditor"),
        INVOICE("Invoice"),
        LET_FEE("Let fee"),
        OWNER("Owner"),
        RENT("Rent"),
        SALE("Sale"),
        WATER("Water"),
        DEPOSIT("Deposit"),
        INVEST_DEPOSIT("Invest deposit"),
        INVEST_RETURN("Invest return"),
        PROPERTY("Property"),
        INTEREST("Interest"),
        PREP_FEE("Prep fee"),
        SALE_DEPOSIT("Sale deposit")
}


enum class TransactionCategory {
        RECEIPT, PAYMENT, TRANSFER, EFT
}

enum class LedgerRecordType {
        DEBIT, CREDIT
}

enum class LedgerType(val description: String) {
        OWNER("Owner"),
        TENANT("Tenant"),
        SALES_VENDOR("Sales/Vendor"),
        CREDITOR("Creditor"),
        BOND("Bond"),
        BANK("Bank"),
        BOOKING("Booking")
}