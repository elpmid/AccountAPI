package com.console.rentpayment.logging


/**
 * Created by Nick on 4/12/2016.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Loggable (
    val levelsToPropertyLogs: Array<LogLevelToProperties> = arrayOf()
)


