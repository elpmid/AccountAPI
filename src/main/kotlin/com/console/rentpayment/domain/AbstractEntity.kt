package com.console.rentpayment.domain

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractEntity (

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
        var lastModifiedBy: String = ""
)

