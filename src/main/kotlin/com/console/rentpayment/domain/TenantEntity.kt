package com.console.rentpayment.domain

import org.joda.money.Money
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity()
@EntityListeners(AuditingEntityListener::class)
@Table(
        name = "TENANT",
        uniqueConstraints = [
                UniqueConstraint(
                        columnNames = ["lease_name"],
                        name="uk_leaseName"
                )
        ]
)
open class TenantEntity(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        // TODO have database populate this field
        @CreatedDate
        var createdDate : LocalDateTime =  LocalDateTime.now(),

        // TODO have database populate this field
        @LastModifiedDate
        var lastModifiedDate : LocalDateTime = LocalDateTime.now(),

        @CreatedBy
        var createdBy: String = "",

        @LastModifiedBy
        var lastModifiedBy: String = "",

        @Column(name = "LEASE_NAME", nullable = false)
        var leaseName: String,

        @Column(name = "NAME", nullable = false)
        var name: String,

        @Column(name = "WKLY_RENT_AMT", nullable = false)
        var weeklyRentAmount: Money,

        @Column(name = "RENT_DTE_PAID_TO", nullable = false)
        var rentDatePaidTo: LocalDateTime,

        @Column(name = "RENT_CREDIT_AMT", nullable = false)
        var rentCreditAmount: Money,

        @OneToMany(fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
        @JoinColumn(name = "TENANT_ID", nullable = true)
        var rentReceipts: MutableSet<RentReceiptEntity> = linkedSetOf()

)