package com.console.rentpayment

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Created by Nick on 13/12/2016.
 */
@ControllerAdvice
open class ExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun exception(e: DataIntegrityViolationException) : ResponseEntity<String> {

        if (e.cause is ConstraintViolationException) {
            val hibernateException : ConstraintViolationException =  e.cause as org.hibernate.exception.ConstraintViolationException
            val constraintName : String = hibernateException.constraintName
            System.out.println(constraintName)
        }
        return ResponseEntity("Error", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}