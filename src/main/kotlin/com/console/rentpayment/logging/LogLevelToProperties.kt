package com.console.rentpayment.logging

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

/**
 * Created by Nick on 2/01/2017.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LogLevelToProperties(
    val logLevel: LogLevel,
    val properties: Array<String>
)


