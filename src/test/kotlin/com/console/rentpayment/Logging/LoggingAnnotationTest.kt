package com.console.rentpayment.Logging

/**
 * Created by nkoutouridis on 9/01/2017.
 */

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.slf4j.LoggerFactory
import org.springframework.aop.Advisor
import org.springframework.aop.aspectj.AspectJExpressionPointcut
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.stereotype.Service
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import kotlin.reflect.KClass
import com.console.rentpayment.logging.Loggable2
import com.console.rentpayment.logging.LoggingAspect2
import com.jcabi.matchers.RegexMatchers.matchesPattern
import com.nhaarman.mockito_kotlin.times
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.MatcherAssert.assertThat


import org.mockito.verification.VerificationMode

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes=arrayOf(TestConfig::class))
@TestPropertySource(properties = arrayOf("logging.level.org.springframework.security:INFO"))
class LoggingAnnotationTest {


    @Autowired lateinit var loggableAnnotationTester: LoggableAnnotationTester

    @Mock
    private val mockAppender: Appender<ILoggingEvent>? = null

    //Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent

    @Captor
    private val captorLoggingEvent: ArgumentCaptor<LoggingEvent>? = null

    @Before
    fun setup() {
        val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        logger.addAppender(mockAppender)
    }

    @After
    fun teardown() {
        val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        logger.detachAppender(mockAppender)
    }


    @Test
    fun `test Loggable annotation no parameters set, on a method with no parameters and no return value`() {

        //This should log at the level of the logger - no parameters or retrun value will be logged

        LoggingTestUtils.setLogLevel(LoggableAnnotationTester::class, Level.INFO)

        loggableAnnotationTester.testAnnotationWithNoParametersOnMethodWithNoParametersAndNoReturnValue()

        verify(mockAppender, times(2))!!.doAppend(captorLoggingEvent!!.capture())

        val loggingEvents: List<LoggingEvent> = captorLoggingEvent.allValues

        assertThat(loggingEvents, everyItem(hasProperty("level", equalTo(Level.INFO))))
        assertThat(loggingEvents.first().formattedMessage, containsString("Entering method 'testAnnotationWithNoParametersOnMethodWithNoParametersAndNoReturnValue()' " +
                                                                          "of class [com.console.rentpayment.Logging.LoggableAnnotationTester]"))

       assertThat(loggingEvents.elementAt(1).formattedMessage, matchesPattern("\\AExiting method 'testAnnotationWithNoParametersOnMethodWithNoParametersAndNoReturnValue' " +
                                                                                          "of class \\[com.console.rentpayment.Logging.LoggableAnnotationTester\\] returned '' took (\\d+)ms.\\Z"))
    }

    @Test
    fun `test Loggable annotation no parameters set, on a method with a parameter and a return value`() {

        //This should log at the level of the logger - no parameters or retrun value will be logged

        LoggingTestUtils.setLogLevel(LoggableAnnotationTester::class, Level.INFO)

        loggableAnnotationTester.testAnnotationWithNoParametersOnMethodWithParameterAndReturnValue("Not Shown")

        verify(mockAppender, times(2))!!.doAppend(captorLoggingEvent!!.capture())

        val loggingEvents: List<LoggingEvent> = captorLoggingEvent.allValues

        assertThat(loggingEvents, everyItem(hasProperty("level", equalTo(Level.INFO))))
        assertThat(loggingEvents.first().formattedMessage, containsString("Entering method 'testAnnotationWithNoParametersOnMethodWithParameterAndReturnValue()' " +
                                                                          "of class [com.console.rentpayment.Logging.LoggableAnnotationTester]"))

        assertThat(loggingEvents.elementAt(1).formattedMessage, matchesPattern("\\AExiting method 'testAnnotationWithNoParametersOnMethodWithParameterAndReturnValue' " +
                                                                               "of class \\[com.console.rentpayment.Logging.LoggableAnnotationTester\\] returned '' took (\\d+)ms.\\Z"))
    }

}


@Service
open class LoggableAnnotationTester() {

    @Loggable2
    open fun testAnnotationWithNoParametersOnMethodWithNoParametersAndNoReturnValue()  {
        //NOOP
    }

    @Loggable2
    open fun testAnnotationWithNoParametersOnMethodWithParameterAndReturnValue(name: String) : String  {
        return "Hello $name"
    }


}

@Configurable
@EnableAspectJAutoProxy
open class TestConfig() {

    @Bean
    open fun loggableAnnotationTester(): LoggableAnnotationTester = LoggableAnnotationTester()


    @Bean
    open fun loggingInterceptor2(): LoggingAspect2 {
        val cti: LoggingAspect2 = LoggingAspect2()
        cti.setUseDynamicLogger(true)
        cti.setEnterMessage("Entering method '${CustomizableTraceInterceptor.PLACEHOLDER_METHOD_NAME}(${CustomizableTraceInterceptor.PLACEHOLDER_ARGUMENTS})' of class [${CustomizableTraceInterceptor.PLACEHOLDER_TARGET_CLASS_NAME}]")
        cti.setExitMessage("Exiting method '" + CustomizableTraceInterceptor.PLACEHOLDER_METHOD_NAME + "' of class [" + CustomizableTraceInterceptor.PLACEHOLDER_TARGET_CLASS_NAME + "] returned '${CustomizableTraceInterceptor.PLACEHOLDER_RETURN_VALUE}' took " + CustomizableTraceInterceptor.PLACEHOLDER_INVOCATION_TIME + "ms.")
        return cti
    }


    @Bean
    open fun loggingAdvisor2() : Advisor {
        val pointcut = AspectJExpressionPointcut()
        pointcut.expression = "execution(public * *(..)) && @annotation(com.console.rentpayment.logging.Loggable2)"
        return DefaultPointcutAdvisor(pointcut, loggingInterceptor2())
    }
}


fun <T> verify(mock: T): T = Mockito.verify(mock)!!
fun <T> verify(mock: T, mode: VerificationMode): T = Mockito.verify(mock, mode)!!

object LoggingTestUtils {

    /**
     * Set the logging level for a class.
     *
     * This should only be used for tests - it allows you to override a class' log level during a single test
     * suite/case as opposed to configuring it globally which may be too verbose (e.g. src/test/resources/logback.xml).
     *
     * Usage example:
     *
     * @Before
     * fun setUp() {
     *     originalLogLevel = LoggingUtils.setLogLevel(MyServiceImpl::class, Level.TRACE)
     * }
     *
     * @After
     * fun tearDown() {
     *     LoggingUtils.setLogLevel(MyServiceImpl::class, originalLogLevel)
     * }
     *
     * @param clazz the class to configure
     * @param newLevel the new log level to configure
     * @return the original logging level of the class
     */
    fun setLogLevel(clazz: KClass<*>, newLevel: Level?) : Level? {
        if (newLevel == null) return null  // no-op

        // slf4j doesn't support levels so assume logback
        with(LoggerFactory.getLogger(clazz.java) as Logger) {
            val originalLevel = level
            level = newLevel
            return originalLevel
        }
    }
}
