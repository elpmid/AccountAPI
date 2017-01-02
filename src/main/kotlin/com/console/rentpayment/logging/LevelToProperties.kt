package com.console.rentpayment.logging

/**
 * Created by Nick on 2/01/2017.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LevelToProperties(

        val level: Level,
        val properties : Array<String>
)