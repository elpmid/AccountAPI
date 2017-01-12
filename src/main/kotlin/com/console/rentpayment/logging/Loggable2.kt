package com.console.rentpayment.logging

/**
 * Created by nkoutouridis on 9/01/2017.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Loggable2 (

    val debugProperties: Array<String> = arrayOf(LoggableNull.NULL),
    val infoProperties: Array<String> = arrayOf(LoggableNull.NULL),
    val warnProperties: Array<String> = arrayOf(LoggableNull.NULL),
    val errorProperties: Array<String> = arrayOf(LoggableNull.NULL),
    val fatalProperties: Array<String> = arrayOf(LoggableNull.NULL)
)

internal object LoggableNull {
    //see: https://youtrack.jetbrains.com/issue/KT-11941 why this is a const
    const val NULL  = "THIS IS A SPECIAL NULL VALUE - DO NOT USE"
}