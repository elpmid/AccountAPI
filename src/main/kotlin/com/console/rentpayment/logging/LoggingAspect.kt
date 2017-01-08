package com.console.rentpayment.logging

import org.aopalliance.intercept.MethodInvocation
import org.apache.commons.logging.Log
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.Constants
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.EvaluationContext
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
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
     * The `Pattern` used to match placeholders.
     */
    private val PATTERN = Pattern.compile("\\$\\[\\p{Alpha}+\\]")

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

    private val elParser: ExpressionParser = SpelExpressionParser()

    companion object {
        val LOGGER_LOG_LEVEL_TO_INCLUDED_LOG_LEVELS: Map<LogLevel, List<LogLevel>> =
            hashMapOf(LogLevel.TRACE to listOf<LogLevel>(LogLevel.TRACE, LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR, LogLevel.FATAL),
                      LogLevel.DEBUG to listOf<LogLevel>(LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR, LogLevel.FATAL),
                      LogLevel.INFO to listOf<LogLevel>(LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR, LogLevel.FATAL),
                      LogLevel.WARN to listOf<LogLevel>(LogLevel.WARN, LogLevel.ERROR, LogLevel.FATAL),
                      LogLevel.ERROR to listOf<LogLevel>(LogLevel.ERROR, LogLevel.FATAL),
                      LogLevel.FATAL to listOf<LogLevel>(LogLevel.FATAL))

        /**
         * The `Set` of allowed placeholders.
         */
        val ALLOWED_PLACEHOLDERS = Constants(CustomizableTraceInterceptor::class.java).getValues("PLACEHOLDER_")
    }


    /**
     * The interceptor is enabled if the @Loggable annotation is on the method
     */
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
        val method: Method = invocation.method
        val loggableAnnotation: Loggable = method.getAnnotation(Loggable::class.java)
        val logLevelsToProperties: Array<LogLevelToProperties> = loggableAnnotation.levelsToPropertyLogs
        val levelToLogAt: LogLevel = determineLevelToLogAt(invocation, logLevelsToProperties)
        val propertyNamesFromAnnotation: List<String> = getPropertyNamesFromAnnotation(levelToLogAt, logLevelsToProperties)
        try {
            stopWatch.start(name)
            writeToLog(logger, replacePlaceholders(enterMessage, invocation, null, null, -1, propertyNamesFromAnnotation), null, levelToLogAt)
            returnValue = invocation.proceed()
            return returnValue
        } catch (ex: Throwable) {
            if (stopWatch.isRunning) stopWatch.stop()
            exitThroughException = true
            writeToLog(logger, replacePlaceholders(exceptionMessage, invocation, null, ex, stopWatch.totalTimeMillis, propertyNamesFromAnnotation), ex, levelToLogAt)
            throw ex
        } finally {
            if (!exitThroughException) {
                if (stopWatch.isRunning) stopWatch.stop()
                writeToLog(logger, replacePlaceholders(exitMessage, invocation, returnValue, null, stopWatch.totalTimeMillis, propertyNamesFromAnnotation), null, levelToLogAt)
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
    @Cacheable
     fun replacePlaceholders(message: String, methodInvocation: MethodInvocation,
                                     returnValue: Any?, throwable: Throwable?, invocationTime: Long,
                                     propertyNamesFromAnnotation: List<String>): String {

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
               matcher.appendReplacement(output, getArgumentsAndValues(returnValue, methodInvocation.method, methodInvocation.arguments,
                                          propertyNamesFromAnnotation, { !it.startsWith("#result") } ))
            } else if (PLACEHOLDER_ARGUMENT_TYPES == match) {
                appendArgumentTypes(methodInvocation, matcher, output)
            } else if (PLACEHOLDER_RETURN_VALUE == match) {
                matcher.appendReplacement(output, getArgumentsAndValues(returnValue, methodInvocation.method, methodInvocation.arguments,
                                          propertyNamesFromAnnotation, { it.startsWith("#result") } ))
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


    /**
     * Writes the supplied message and [Throwable] to the
     * supplied `Log` instance.
     */
     private fun writeToLog(logger: Log, message: String, ex: Throwable?, levelToLogAt: LogLevel) {
        when(levelToLogAt) {
            LogLevel.TRACE -> if (ex != null) logger.trace(message, ex) else logger.trace(message)
            LogLevel.DEBUG -> if (ex != null) logger.debug(message, ex) else logger.debug(message)
            LogLevel.INFO -> if (ex != null) logger.info(message, ex) else logger.info(message)
            LogLevel.WARN -> if (ex != null) logger.warn(message, ex) else logger.warn(message)
            LogLevel.ERROR -> if (ex != null) logger.error(message, ex) else logger.error(message)
            LogLevel.FATAL -> if (ex != null) logger.fatal(message, ex) else logger.fatal(message)
            LogLevel.NO_LEVEL -> {} //NOOP
        }
    }


    /*
     * Get the names of the properties we want to log from the annotation
     */
    private fun getPropertyNamesFromAnnotation(logLevel: LogLevel, logLevelsToProperty: Array<LogLevelToProperties>) : List<String> {
        return logLevelsToProperty.filter { it.logLevel == logLevel  }.first().properties.toList()
    }

    /*
     * For each property defined on the annotation filtered by the predicate return its value seperated by a comma
     */
    private fun getArgumentsAndValues(returnValue: Any?, method: Method, arguments: Array<Any>, propertyNamesFromAnnotation: List<String>, propertyNamePredicate: (String) -> Boolean) : String {

        val evaluationContext: EvaluationContext = if (returnValue != null)MethodBasedEvaluationContext(returnValue, method, arguments, DefaultParameterNameDiscoverer()) else
                                                                           MethodBasedEvaluationContext(returnValue, method, arguments, DefaultParameterNameDiscoverer())

        if (returnValue != null) {
            evaluationContext.setVariable("result", returnValue)

        }
        return propertyNamesFromAnnotation.filter(propertyNamePredicate).map { element -> element + ":" + evaluateExpression(element, evaluationContext) }.joinToString(",")
    }




    /*
     * create and evaluate the expression
     */
    private fun evaluateExpression(expressionString: String, evaluationContext: EvaluationContext) : String {
        val expression = elParser.parseExpression(expressionString)
        return expression.getValue(evaluationContext, String::class.java)
    }

    /*
    * The logLevel to log at will be the lowest log logLevel in the annotation list applicable to the log lowest logLevel of the logger
    * Example: The logger is set at logLevel INFO - This means it can log INFO, WARN, ERROR and FATAL
    *          The annotation has the following log levels: TRACE, WARN, ERROR
    * So the allowed logging levels for this logger on the annotation are WARN and ERROR. We will pick the lowest logLevel WARN
    *
    * If there are no allowed logging levels the we won't log anything
    * Example: The logger is set to FATAL - This means it can only log FATAL
    *          The annotation has the following log levels: TRACE, WARN, ERROR
    * So there are no allowed logging levels, so we don't log anything
    *
     */
    private fun determineLevelToLogAt(methodInvocation: MethodInvocation, logLevelsToProperties: Array<LogLevelToProperties>) : LogLevel {
        val logger: Log = getLoggerForInvocation(methodInvocation)
        //The log level the logger is logging set to
        val loggerLoggingLevel : LogLevel = determineLoggerLoggingLevel(logger)
        //The log levels applicable to the above e.g. if set to INFO then INFO, WARN, ERROR and FATAL
        val loggerLogLevels: List<LogLevel> = LoggingAspect.LOGGER_LOG_LEVEL_TO_INCLUDED_LOG_LEVELS[loggerLoggingLevel]!!
        //The log levels specified on the annotation
        val annotationLogLevels: List<LogLevel> = logLevelsToProperties.map { it.logLevel }
        //All the log levels specified on the annotation in the log levels applicable to the log level the logger is set to - could be none
        val allowedLogLogLevels: List<LogLevel> = annotationLogLevels.filter { loggerLogLevels.contains(it) }

        if (allowedLogLogLevels.isNotEmpty()) {
            return allowedLogLogLevels.min()!!
        }
        return LogLevel.NO_LEVEL
    }

    /**
     * Get the logging level the logger is set to. This will be the lowest level
     * of the levels enabled on the logger
     */
    private fun determineLoggerLoggingLevel(logger: Log) : LogLevel {
        if (logger.isTraceEnabled) return LogLevel.TRACE
        else if (logger.isDebugEnabled) return LogLevel.DEBUG
        else if (logger.isInfoEnabled) return LogLevel.INFO
        else if (logger.isWarnEnabled) return LogLevel.WARN
        else if (logger.isErrorEnabled) return LogLevel.ERROR
        else return LogLevel.FATAL // logger.isFatalEnabled
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
}