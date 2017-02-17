package com.console.rentpayment.domain

import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.Table

/**
 * @author Nickolas Koutouridis (nickolas.koutouridis@console.com.au)
 */

@Entity
@Table(name = "vw_payment_reversed")
class PaymentReversedEntity (

        @Id
        @Column(name = "payment_id")
        val paymentId: Long? = null,

        @Column(name = "reversal_reason")
        val reversalReason: String,

        @Column(name = "reversal_user_id")
        val reversal_user_id: Long,

        @Column(name = "reversal_date_time_utc", insertable = false, updatable = false, nullable = false)
        @Generated(GenerationTime.INSERT)
        val reversalDateTime: LocalDateTime? = null,

        @OneToOne(mappedBy = "paymentReversedEntity")
        val paymentEntity: PaymentEntity? = null
) {

  //  @MapsId
  //  @OneToOne(fetch = FetchType.LAZY)
 //   @JoinColumn(name = "payment_id")
 //   lateinit var paymentEntity: PaymentEntity

}
