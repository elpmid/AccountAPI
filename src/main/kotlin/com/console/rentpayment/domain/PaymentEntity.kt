/*
 * Copyright 2016 Onthehouse Holdings Pty Ltd. All rights reserved. Not to be copied, redistributed
 * or modified without prior written consent of Onthehouse Holdings Pty Ltd.
 */

package com.console.rentpayment.domain



import com.console.rentpayment.domain.PaymentEntityGraphs.EAGER_PAYMENT_ITEMS_TRANSACTION_TRANSACTION_DETAIL_PAYMENT_DETAIL_PAYMENT_REVERSED
import com.console.rentpayment.domain.PaymentEntityGraphs.EAGER_TRANSACTION
import org.springframework.data.jpa.domain.Specifications
import au.com.console.jpaspecificationdsl.*
import com.console.rentpayment.domain.PaymentEntityGraphs.EAGER_PAYMENT_ITEMS_PAYMENT_DETAIL
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.NamedSubgraph
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table

object PaymentEntityGraphs {
        const val EAGER_PAYMENT_ITEMS_TRANSACTION_TRANSACTION_DETAIL_PAYMENT_DETAIL_PAYMENT_REVERSED = "PaymentEntity.eager.paymentItemEntities.transactionEntities." +
                "transactionDetailEntities.paymentDetailEntity.paymentReversedEntity"
        const val EAGER_PAYMENT_ITEMS_PAYMENT_DETAIL = "PaymentEntity.eager.paymentItemEntities.paymentDetailEntity"

        const val EAGER_TRANSACTION = "Transaction"

}
@NamedEntityGraphs(
        NamedEntityGraph(name = EAGER_PAYMENT_ITEMS_TRANSACTION_TRANSACTION_DETAIL_PAYMENT_DETAIL_PAYMENT_REVERSED,
                attributeNodes = arrayOf(
                        NamedAttributeNode(
                                value = "paymentItemEntities",
                                subgraph = EAGER_TRANSACTION),
                        NamedAttributeNode(
                                value = "paymentDetailEntity")//,
                       //NamedAttributeNode(
                       //         value = "paymentReversedEntity")
                ),
                subgraphs = arrayOf(
                        NamedSubgraph(
                                name = EAGER_TRANSACTION,
                                attributeNodes = arrayOf(NamedAttributeNode(value = "transactionEntities", subgraph = "transactionDetailEntity")
                                )
                        ),
                        NamedSubgraph(
                                name = "transactionDetailEntity",
                                attributeNodes = arrayOf(NamedAttributeNode(value = "transactionDetailEntity")
                                )
                        )
                )
        ),
        NamedEntityGraph(name = EAGER_PAYMENT_ITEMS_PAYMENT_DETAIL,
                attributeNodes = arrayOf(NamedAttributeNode(value = "paymentItemEntities"),
                        NamedAttributeNode(
                        value = "paymentDetailEntity")))
)

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

        @OneToOne(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
        @JoinColumn(name="payment_id")
        val paymentReversedEntity: PaymentReversedEntity? = null,


//        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
//        @JoinColumn(name = "payment_id", nullable = false, updatable = false)
//        val paymentItemEntities: MutableSet<PaymentItemEntity>

        @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.LAZY)
        @JoinColumn(name = "payment_id", nullable = false, updatable = false)
        val paymentItemEntities: List<PaymentItemEntity>


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
            paymentItemEntities = mutableListOf()
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

fun joinPaymentPaymentReversalPaymentItemTransactionTransactionDetail(paymentId: Long?): Specifications<TransactionEntity>? = paymentId?.let {
        where {
                equal(it.join(TransactionEntity::transactionDetailEntity).get(TransactionDetailEntity::paymentId), paymentId)
        }

}

