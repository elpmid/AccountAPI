package com.example.domain

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
}

