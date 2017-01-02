package com.console.rentpayment.logging

import org.aopalliance.intercept.MethodInvocation
import org.apache.commons.logging.Log
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.core.Constants
import org.springframework.util.Assert
import org.springframework.util.ClassUtils
import org.springframework.util.StopWatch
import org.springframework.util.StringUtils
import java.lang.reflect.Method
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Nick on 2/01/2017.
 */

open class LoggingAspect() : CustomizableTraceInterceptor() {

    /**
     * The `$[methodName]` placeholder.
     * Replaced with the name of the method being invoked.
     */
    val PLACEHOLDER_METHOD_NAME = "$[methodName]"

    /**
     * The `$[targetClassName]` placeholder.
     * Replaced with the fully-qualifed name of the `Class`
     * of the method invocation target.
     */
    val PLACEHOLDER_TARGET_CLASS_NAME = "$[targetClassName]"

    /**
     * The `$[targetClassShortName]` placeholder.
     * Replaced with the short name of the `Class` of the
     * method invocation target.
     */
    val PLACEHOLDER_TARGET_CLASS_SHORT_NAME = "$[targetClassShortName]"

    /**
     * The `$[returnValue]` placeholder.
     * Replaced with the `String` representation of the value
     * returned by the method invocation.
     */
    val PLACEHOLDER_RETURN_VALUE = "$[returnValue]"

    /**
     * The `$[argumentTypes]` placeholder.
     * Replaced with a comma-separated list of the argument types for the
     * method invocation. Argument types are written as short class names.
     */
    val PLACEHOLDER_ARGUMENT_TYPES = "$[argumentTypes]"

    /**
     * The `$[arguments]` placeholder.
     * Replaced with a comma separated list of the argument values for the
     * method invocation. Relies on the `toString()` method of
     * each argument type.
     */
    val PLACEHOLDER_ARGUMENTS = "$[arguments]"

    /**
     * The `$[exception]` placeholder.
     * Replaced with the `String` representation of any
     * `Throwable` raised during method invocation.
     */
    val PLACEHOLDER_EXCEPTION = "$[exception]"

    /**
     * The `$[invocationTime]` placeholder.
     * Replaced with the time taken by the invocation (in milliseconds).
     */
    val PLACEHOLDER_INVOCATION_TIME = "$[invocationTime]"

    /**
     * The default message used for writing method entry messages.
     */
    private val DEFAULT_ENTER_MESSAGE =
            "Entering method '$PLACEHOLDER_METHOD_NAME' of class [$PLACEHOLDER_TARGET_CLASS_NAME]"

    /**
     * The default message used for writing method exit messages.
     */
    private val DEFAULT_EXIT_MESSAGE =
            "Exiting method '$PLACEHOLDER_METHOD_NAME' of class [$PLACEHOLDER_TARGET_CLASS_NAME]"

    /**
     * The default message used for writing exception messages.
     */
    private val DEFAULT_EXCEPTION_MESSAGE =
            "Exception thrown in method '$PLACEHOLDER_METHOD_NAME' of class [$PLACEHOLDER_TARGET_CLASS_NAME]"

    /**
     * The `Pattern` used to match placeholders.
     */
    private val PATTERN = Pattern.compile("\\$\\[\\p{Alpha}+\\]")

    /**
     * The `Set` of allowed placeholders.
     */
    private val ALLOWED_PLACEHOLDERS = Constants(CustomizableTraceInterceptor::class.java).getValues("PLACEHOLDER_")


    /**
     * The message for method entry.
     */
    private var enterMessage = DEFAULT_ENTER_MESSAGE

    /**
     * The message for method exit.
     */
    private var exitMessage = DEFAULT_EXIT_MESSAGE

    /**
     * The message for exceptions during method execution.
     */
    private var exceptionMessage = DEFAULT_EXCEPTION_MESSAGE


    /**
     * Set the template used for method entry log messages.
     * This template can contain any of the following placeholders:
     *
     *  * `$[targetClassName]`
     *  * `$[targetClassShortName]`
     *  * `$[argumentTypes]`
     *  * `$[arguments]`
     *
     */
    override fun setEnterMessage(enterMessage: String) {
        Assert.hasText(enterMessage, "'enterMessage' must not be empty")
        checkForInvalidPlaceholders(enterMessage)
        Assert.doesNotContain(enterMessage, PLACEHOLDER_RETURN_VALUE,
                "enterMessage cannot contain placeholder [$PLACEHOLDER_RETURN_VALUE]")
        Assert.doesNotContain(enterMessage, PLACEHOLDER_EXCEPTION,
                "enterMessage cannot contain placeholder [$PLACEHOLDER_EXCEPTION]")
        Assert.doesNotContain(enterMessage, PLACEHOLDER_INVOCATION_TIME,
                "enterMessage cannot contain placeholder [$PLACEHOLDER_INVOCATION_TIME]")
        this.enterMessage = enterMessage
    }

    /**
     * Set the template used for method exit log messages.
     * This template can contain any of the following placeholders:
     *
     *  * `$[targetClassName]`
     *  * `$[targetClassShortName]`
     *  * `$[argumentTypes]`
     *  * `$[arguments]`
     *  * `$[returnValue]`
     *  * `$[invocationTime]`
     *
     */
    override fun setExitMessage(exitMessage: String) {
        Assert.hasText(exitMessage, "'exitMessage' must not be empty")
        checkForInvalidPlaceholders(exitMessage)
        Assert.doesNotContain(exitMessage, PLACEHOLDER_EXCEPTION,
                "exitMessage cannot contain placeholder [$PLACEHOLDER_EXCEPTION]")
        this.exitMessage = exitMessage
    }


    /**
     * Set the template used for method exception log messages.
     * This template can contain any of the following placeholders:
     *
     *  * `$[targetClassName]`
     *  * `$[targetClassShortName]`
     *  * `$[argumentTypes]`
     *  * `$[arguments]`
     *  * `$[exception]`
     *
     */
    override fun setExceptionMessage(exceptionMessage: String) {
        Assert.hasText(exceptionMessage, "'exceptionMessage' must not be empty")
        checkForInvalidPlaceholders(exceptionMessage)
        Assert.doesNotContain(exceptionMessage, PLACEHOLDER_RETURN_VALUE,
                "exceptionMessage cannot contain placeholder [$PLACEHOLDER_RETURN_VALUE]")
        Assert.doesNotContain(exceptionMessage, PLACEHOLDER_INVOCATION_TIME,
                "exceptionMessage cannot contain placeholder [$PLACEHOLDER_INVOCATION_TIME]")
        this.exceptionMessage = exceptionMessage
    }

    override fun writeToLog(logger: Log, message: String, ex: Throwable?) {
        if (ex != null) {
            logger.info(message, ex)
        } else {
            logger.info(message)
        }
    }

    override fun isInterceptorEnabled(methodInvocation: MethodInvocation, logger: Log): Boolean {

        val method : Method = methodInvocation.method
        if (method.isAnnotationPresent(Loggable::class.java)) {
            return true

        }
        return false

    }


    /**
     * Writes a log message before the invocation based on the value of `enterMessage`.
     * If the invocation succeeds, then a log message is written on exit based on the value
     * `exitMessage`. If an exception occurs during invocation, then a message is
     * written based on the value of `exceptionMessage`.
     * @see .setEnterMessage

     * @see .setExitMessage

     * @see .setExceptionMessage
     */
    override fun invokeUnderTrace(invocation: MethodInvocation, logger: Log): Any {
        val name = ClassUtils.getQualifiedMethodName(invocation.method)
        val stopWatch = StopWatch(name)
        var returnValue: Any? = null
        var exitThroughException = false
        try {
            writeToLog(logger,
                    replacePlaceholders(logger, this.enterMessage, invocation, null, null, -1))
            returnValue = invocation.proceed()
            return returnValue
        } catch (ex: Throwable) {
            exitThroughException = true
            writeToLog(logger, replacePlaceholders(logger, this.exceptionMessage, invocation, null, ex, stopWatch.totalTimeMillis), ex)
            throw ex
        } finally {
            if (!exitThroughException) {
                writeToLog(logger, replacePlaceholders(logger, this.exitMessage, invocation, returnValue, null, stopWatch.totalTimeMillis))
            }
        }
    }



    /**
     * Replace the placeholders in the given message with the supplied values,
     * or values derived from those supplied.
     * @param message the message template containing the placeholders to be replaced
     * *
     * @param methodInvocation the `MethodInvocation` being logged.
     * * Used to derive values for all placeholders except `$[exception]`
     * * and `$[returnValue]`.
     * *
     * @param returnValue any value returned by the invocation.
     * * Used to replace the `$[returnValue]` placeholder. May be `null`.
     * *
     * @param throwable any `Throwable` raised during the invocation.
     * * The value of `Throwable.toString()` is replaced for the
     * * `$[exception]` placeholder. May be `null`.
     * *
     * @param invocationTime the value to write in place of the
     * * `$[invocationTime]` placeholder
     * *
     * @return the formatted output to write to the log
     */
     fun replacePlaceholders(logger: Log, message: String, methodInvocation: MethodInvocation,
                                     returnValue: Any?, throwable: Throwable?, invocationTime: Long): String {

        val matcher = PATTERN.matcher(message)

        val output = StringBuffer()
        while (matcher.find()) {
            val match = matcher.group()
            if (PLACEHOLDER_METHOD_NAME == match) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(methodInvocation.method.name))
            } else if (PLACEHOLDER_TARGET_CLASS_NAME == match) {
                val className = getClassForLogging(methodInvocation.`this`).name
                matcher.appendReplacement(output, Matcher.quoteReplacement(className))
            } else if (PLACEHOLDER_TARGET_CLASS_SHORT_NAME == match) {
                val shortName = ClassUtils.getShortName(getClassForLogging(methodInvocation.`this`))
                matcher.appendReplacement(output, Matcher.quoteReplacement(shortName))
            } else if (PLACEHOLDER_ARGUMENTS == match) {

                val method : Method = methodInvocation.method
                if (method.isAnnotationPresent(Loggable::class.java)) {
                    val loggableAnnotation : Loggable = method.getAnnotation(Loggable::class.java)
                    if (loggableAnnotation.levelsToProperties.isNotEmpty()) {

                        val annotationLevels : List<Level> = loggableAnnotation.levelsToProperties.map { it.level }
                        val levelToLogAt : Level = determineLevelToLogAt(determineLoggerLowestLevel(logger), annotationLevels)
                     //   val out : String = getPropertiesForLevel(levelToLogAt, loggableAnnotation.levelsToProperties)
                    } else {
                        matcher.appendReplacement(output,
                                Matcher.quoteReplacement(StringUtils.arrayToCommaDelimitedString(methodInvocation.arguments)))
                    }
                }


            } else if (PLACEHOLDER_ARGUMENT_TYPES == match) {
                appendArgumentTypes(methodInvocation, matcher, output)
            } else if (PLACEHOLDER_RETURN_VALUE == match) {
                appendReturnValue(methodInvocation, matcher, output, returnValue)
            } else if (throwable != null && PLACEHOLDER_EXCEPTION == match) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(throwable.toString()))
            } else if (PLACEHOLDER_INVOCATION_TIME == match) {
                matcher.appendReplacement(output, java.lang.Long.toString(invocationTime))
            } else {
                // Should not happen since placeholders are checked earlier.
                throw IllegalArgumentException("Unknown placeholder [$match]")
            }
        }
        matcher.appendTail(output)

        return output.toString()
    }


    fun determineLoggerLowestLevel(logger: Log) : Level {
        if (logger.isTraceEnabled)
            return Level.TRACE
        else if (logger.isDebugEnabled)
            return Level.DEBUG
        else if (logger.isInfoEnabled)
            return Level.INFO
        else if (logger.isWarnEnabled)
            return Level.WARN
        else if (logger.isErrorEnabled)
            return Level.ERROR
        else // logger.isFatalEnabled
            return Level.FATAL

    }


    fun getPropertiesForLevel(level: Level, levelToProperties: LevelToProperties) : String {



            return ""
    }


    fun determineLevelToLogAt(loggerLowestLevel : Level, annotationLevels : List<Level>) : Level {

        val allowedLevels : Map<Level, List<Level>> = hashMapOf(Level.TRACE to listOf<Level>(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL),
                                                                Level.DEBUG to listOf<Level>(Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL),
                                                                Level.INFO to listOf<Level>(Level.INFO, Level.WARN, Level.ERROR, Level.FATAL),
                                                                Level.WARN to listOf<Level>(Level.WARN, Level.ERROR, Level.FATAL),
                                                                Level.ERROR to listOf<Level>(Level.ERROR, Level.FATAL),
                                                                Level.FATAL to listOf<Level>(Level.FATAL))

        val allowedLevelsForLowestLevel: List<Level>? = allowedLevels[loggerLowestLevel]

        val annotationAllowedLevels: List<Level> = annotationLevels.filter { isInAllowedLevels(it, allowedLevelsForLowestLevel!!)}


        var min: Int = Level.OFF.level
        for (level in annotationAllowedLevels)
            min = Math.min(level.level, min);


        val lowestAnnotationAllowedLevel : Level =   if (min == Integer.MIN_VALUE) Level.OFF else Level.values()[min]

        return lowestAnnotationAllowedLevel

    }

    private fun isInAllowedLevels(level: Level, allowedLevelsForLowestLevel: List<Level>) : Boolean {
        return allowedLevelsForLowestLevel!!.contains(level)
    }


    /**
     * Adds the `String` representation of the method return value
     * to the supplied `StringBuffer`. Correctly handles
     * `null` and `void` results.
     * @param methodInvocation the `MethodInvocation` that returned the value
     * *
     * @param matcher the `Matcher` containing the matched placeholder
     * *
     * @param output the `StringBuffer` to write output to
     * *
     * @param returnValue the value returned by the method invocation.
     */
    private fun appendReturnValue(methodInvocation: MethodInvocation, matcher: Matcher, output: StringBuffer, returnValue: Any?) {
        //super<CustomizableTraceInterceptor>.



        if (methodInvocation.method.returnType == Void.TYPE) {
            matcher.appendReplacement(output, "void")
        } else if (returnValue == null) {
            matcher.appendReplacement(output, "null")
        } else {
            matcher.appendReplacement(output, Matcher.quoteReplacement(returnValue.toString()))
        }
    }

    /**
     * Adds a comma-separated list of the short `Class` names of the
     * method argument types to the output. For example, if a method has signature
     * `put(java.lang.String, java.lang.Object)` then the value returned
     * will be `String, Object`.
     * @param methodInvocation the `MethodInvocation` being logged.
     * * Arguments will be retrieved from the corresponding `Method`.
     * *
     * @param matcher the `Matcher` containing the state of the output
     * *
     * @param output the `StringBuffer` containing the output
     */
    private fun appendArgumentTypes(methodInvocation: MethodInvocation, matcher: Matcher, output: StringBuffer) {
        val argumentTypes = methodInvocation.method.parameterTypes
        val argumentTypeShortNames = arrayOfNulls<String>(argumentTypes.size)
        for (i in argumentTypeShortNames.indices) {
            argumentTypeShortNames[i] = ClassUtils.getShortName(argumentTypes[i])
        }
        matcher.appendReplacement(output,
                Matcher.quoteReplacement(StringUtils.arrayToCommaDelimitedString(argumentTypeShortNames)))
    }

    /**
     * Checks to see if the supplied `String` has any placeholders
     * that are not specified as constants on this class and throws an
     * `IllegalArgumentException` if so.
     */
    @Throws(IllegalArgumentException::class)
    private fun checkForInvalidPlaceholders(message: String) {
        val matcher = PATTERN.matcher(message)
        while (matcher.find()) {
            val match = matcher.group()
            if (!ALLOWED_PLACEHOLDERS.contains(match)) {
                throw IllegalArgumentException("Placeholder [$match] is not valid")
            }
        }
    }


}