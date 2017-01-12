package com.console.rentpayment.logging

/**
 * Created by nkoutouridis on 9/01/2017.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LogLevelToProperties(
    val logLevel: LogLevel,
    val properties: Array<String>
)