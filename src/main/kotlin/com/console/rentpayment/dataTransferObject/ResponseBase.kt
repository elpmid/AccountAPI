package com.console.rentpayment.dataTransferObject


import java.time.LocalDateTime

open class ResponseBase(
        val id: Long?,
        val createdDate: LocalDateTime,
        val lastModifiedDate: LocalDateTime,
        val createdBy: String,
        val lastModifiedBy: String
)
