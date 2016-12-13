package com.console.rentpayment

import com.console.rentpayment.dataTransferObject.MoneyDeserializer
import com.console.rentpayment.dataTransferObject.MoneySerializer
import com.example.domain.AuditorAwareImpl
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.joda.money.Money
import org.springframework.aop.Advisor
import org.springframework.aop.aspectj.AspectJExpressionPointcut
import org.springframework.aop.interceptor.CustomizableTraceInterceptor
import org.springframework.aop.interceptor.CustomizableTraceInterceptor.*
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter






@SpringBootApplication
@EnableJpaAuditing
open class AccountApiApplication {

    @Bean
    open fun auditorProvider(): AuditorAware<String> {
        return AuditorAwareImpl()
    }


    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder()
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        builder.modulesToInstall(KotlinModule(), javaTimeModule)
        builder.deserializerByType(Money::class.java, MoneyDeserializer())
        builder.serializerByType(Money::class.java, MoneySerializer())
        return builder
    }


    @Bean
    open fun customizableTraceInterceptor(): CustomizableTraceInterceptor {
        val cti = CustomizableTraceInterceptor()
        cti.setEnterMessage("Entering method '$PLACEHOLDER_METHOD_NAME($PLACEHOLDER_ARGUMENTS)' of class [$PLACEHOLDER_TARGET_CLASS_NAME]")
        cti.setExitMessage("Exiting method '" + PLACEHOLDER_METHOD_NAME + "' of class [" + PLACEHOLDER_TARGET_CLASS_NAME + "] returned '$PLACEHOLDER_RETURN_VALUE' took " + PLACEHOLDER_INVOCATION_TIME + "ms.")

        return cti
    }



    @Bean
    open fun traceAdvisor(): Advisor {
        val pointcut = AspectJExpressionPointcut()
        pointcut.expression = "execution(public * *(..)) && @annotation(com.console.rentpayment.logging.Loggable)"
        return DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor())
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(AccountApiApplication::class.java, *args)
}