package com.console.rentpayment.domain

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*


/**
 * Created by Nick on 9/11/2016.
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @CreatedDate
    var createdDate : LocalDateTime =  LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate : LocalDateTime = LocalDateTime.now()

    @CreatedBy
    var createdBy: String = ""

    @LastModifiedBy
    var lastModifiedBy: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}

