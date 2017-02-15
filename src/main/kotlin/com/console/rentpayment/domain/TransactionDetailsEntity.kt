package com.console.rentpayment.domain

import com.console.rentpayment.domain.TransactionEntity
import javax.persistence.*

/**
 * @author Murray Cole (mcole@console.com.au)
 */
@Entity
@Table(name = "vw_transaction_detail")
data class TransactionDetailEntity(
        @Id
        @Column(name = "transaction_id")
        val transactionId: Long? = null,

        @Column(name = "transaction_methods")
        val transactionMethods: String,

        @Column(name = "payment_id")
        val paymentId: Long? = null,

        @Column(name = "receipt_id")
        val receiptId: Long? = null,

        @Column(name = "source_reference")
        val sourceReference: String,

        @Column(name = "source_description")
        val sourceDescription: String,

        @Column(name = "destination_reference")
        val destinationReference: String? = null,

        @Column(name = "destination_description")
        val destinationDescription: String? = null,

        @Column(name = "description")
        val details: String,

        @Column(name = "property_id")
        val propertyId: Long? = null,

        @Column(name = "dissection_id")
        val dissectionId: Long? = null,

        @Column(name = "created_by_user_id")
        val createdByUserId: Long

) {

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    lateinit var transactionEntity: TransactionEntity

    /**
     * Default constructor for Hibernate.
     */
    private constructor () : this(
            transactionMethods = "",
            sourceReference = "",
            sourceDescription = "",
            details = "",
            createdByUserId = 0L
    )

}