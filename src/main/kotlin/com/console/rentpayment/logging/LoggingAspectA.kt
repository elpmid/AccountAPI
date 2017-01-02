package com.console.rentpayment.logging

import org.aopalliance.intercept.MethodInvocation
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils
import java.util.regex.Matcher


/**
 * Created by Nick on 4/12/2016.
 */

//@Aspect
//@Component
//@Named
 class LoggingAspectA() {
    private val logger : Logger = LoggerFactory.getLogger(LoggingAspectA::class.java)

    @Pointcut("execution(public * *(..))")
    fun publicMethod() {
    }

    @Pointcut("@annotation(Loggable)")
    fun methodLoggable() {
    }

    @Pointcut("publicMethod() && methodLoggable()")
    fun publicAndLoggableMethod() {
    }

    @Before("publicAndLoggableMethod()")
    fun logServiceCall(joinPoint: JoinPoint) {
        logger.info("Call      " + this.generateMethodCallDescription(joinPoint) + " - Arguments - " + ReflectionHelper.generateMethodArgumentsDescription(joinPoint))
    }

    @AfterReturning(value = "publicAndLoggableMethod()")
    fun logServiceReturn(joinPoint: JoinPoint) {
        logger.info("Return    " + this.generateMethodCallDescription(joinPoint) + " - Success")
    }

    @AfterThrowing(value = "publicAndLoggableMethod()", throwing = "ex")
    fun logServiceException(joinPoint: JoinPoint, ex: Throwable) {
        logger.info("Exception " + this.generateMethodCallDescription(joinPoint) + " - Error - " + ex.javaClass.simpleName + " - " + ex.message)
    }

    private fun generateMethodCallDescription(joinPoint: JoinPoint): String {
        val builder = StringBuilder()

        val aClass = joinPoint.signature.declaringType
        val method = joinPoint.signature as MethodSignature

        val className = aClass.simpleName
        val methodName = method.getName()

        builder.append(className)
        builder.append(" - ")
        builder.append(methodName)

        return builder.toString()
    }




}