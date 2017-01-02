package com.console.rentpayment.logging

/**
 * Created by Nick on 2/01/2017.
 */
enum class Level(val level: Int) {

    /**
     * TRACE level of logging.
     */
    TRACE(0),

    /**
     * DEBUG level of logging.
     */
    DEBUG(1),

    /**
     * INFO level of logging.
     */
    INFO(2),

    /**
     * WARN level of logging.
     */
    WARN(3),

    /**
     * ERROR level of logging.
     */
    ERROR(4),

    /**
     * FATAL level of logging
     */
    FATAL(5),

    OFF(6)

}



