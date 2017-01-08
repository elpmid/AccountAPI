package com.console.rentpayment.logging

import java.lang.annotation.RetentionPolicy



/**
 * Created by Nick on 2/01/2017.
 */
enum class LogLevel(val level: Int) {

    /**
     * TRACE logLevel of logging.
     */
    TRACE(0),

    /**
     * DEBUG logLevel of logging.
     */
    DEBUG(1),

    /**
     * INFO logLevel of logging.
     */
    INFO(2),

    /**
     * WARN logLevel of logging.
     */
    WARN(3),

    /**
     * ERROR logLevel of logging.
     */
    ERROR(4),

    /**
     * FATAL logLevel of logging
     */
    FATAL(5),

    NO_LEVEL(6)

}
