/*
 * Copyright 2016 Onthehouse Holdings Pty Ltd. All rights reserved. Not to be copied, redistributed
 * or modified without prior written consent of Onthehouse Holdings Pty Ltd.
 */

package com.console.rentpayment.domain



import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

/**
 * @author Alan Loi (alan.loi@console.com.au)
 */
@Entity
@Table(name = "vw_payment")
data class PaymentEntity(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "payment_id")
        val paymentId: Long? = null,

        @Column(name = "payment_type")
        @Enumerated(EnumType.STRING)
        val type: PaymentType,

        @Column(name = "payment_date")
        val paymentDate: LocalDate,

        @Column(name = "payment_amount")
        val paymentAmount: Long,

        val reference: String,

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @PrimaryKeyJoinColumn
        var paymentDetailEntity: PaymentDetailEntity,

        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
        @JoinColumn(name = "payment_id", nullable = false, updatable = false)
        val items: MutableSet<PaymentItemEntity>

) {

    /**
     * Default constructor for Hibernate.
     */
    private constructor() : this(
            type = PaymentType.JOURNAL,
            paymentDate = LocalDate.MIN,
            paymentAmount = 0,
            reference = "",
            paymentDetailEntity = PaymentDetailEntity(),
            items = mutableSetOf()
    )
}
enum class PaymentType(val description: String) {
        JOURNAL("Journal"),
        EFT("EFT"),
        CHEQUE("Cheque");

        companion object {
                @JvmField val PRESENTABLE_TYPES = listOf(EFT, CHEQUE)
        }
}