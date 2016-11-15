package com.console.rentpayment.dataTransferObject


import java.time.LocalDateTime

/**
 * Created by Nick on 14/11/2016.
 */
abstract class AbstractResponse() {
    constructor(id: Long?, createdDate : LocalDateTime, lastModifiedDate : LocalDateTime,
                createdBy: String, lastModifiedBy: String) : this() {
        this.id = id
        this.createdDate = createdDate
        this.lastModifiedDate = lastModifiedDate
        this.createdBy = createdBy
        this.lastModifiedBy = lastModifiedBy
    }

    var id : Long? = null
    lateinit var createdDate :LocalDateTime
    lateinit var lastModifiedDate : LocalDateTime
    lateinit  var createdBy : String
    lateinit var lastModifiedBy : String

}
