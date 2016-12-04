package com.console.rentpayment.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import java.util.*


/**
 * Created by Nick on 4/12/2016.
 */
class ReflectionHelper {

    companion object {
        @JvmStatic fun getClass(joinPoint: JoinPoint): Class<*> {
            return joinPoint.signature.declaringType
        }

        @JvmStatic fun getMethod(joinPoint: JoinPoint): MethodSignature {
            return joinPoint.signature as MethodSignature
        }

        @JvmStatic fun getMethodArguments(joinPoint: JoinPoint): Map<String, Any> {
            val method : MethodSignature = getMethod(joinPoint)
            val parameterNames = method.parameterNames
            val params = joinPoint.args

            val paramsMap = HashMap<String, Any>()
            for (i in parameterNames.indices) {
                paramsMap.put(parameterNames[i], params[i])
            }

            return paramsMap
        }


        @JvmStatic fun generateMethodArgumentsDescription(joinPoint: JoinPoint): String {
            val builder = StringBuilder()

            val method : MethodSignature = getMethod(joinPoint)

            val parameterTypes : Array<Class<Any>> = method.parameterTypes
            val paramAnnotations : Array<Array<Annotation>> = getMethodAnnotations(joinPoint)
            val params : Array<Any> = joinPoint.args

            builder.append(" ( ")

            for (i in params.indices) {
                val paramDesc = generateParamDescription(parameterTypes[i], params[i], paramAnnotations[i])
                builder.append(paramDesc)
                builder.append(", ")
            }
            builder.delete(builder.length - 2, builder.length)
            builder.append(" )")

            return builder.toString()

        }

        @JvmStatic fun getMethodAnnotations(joinPoint: JoinPoint): Array<Array<Annotation>> {
            val aClass = getClass(joinPoint)
            val method = getMethod(joinPoint)
            val parameterTypes = method.parameterTypes
            val methodName = method.name
            return aClass.getMethod(methodName, *parameterTypes).parameterAnnotations
        }

        private fun generateParamDescription(parameterType: Class<*>, value: Any, annotations: Array<Annotation>): StringBuilder {
            val builder = StringBuilder()
            builder.append(parameterType.simpleName)
            builder.append(": ")

            var canBeLogged = true
            for (annotation in annotations) {
                if (annotation is NotLog) canBeLogged = false
            }

            if (canBeLogged) {
                builder.append(getObjectDescription(value))
            } else {
                builder.append("****")
            }

            return builder
        }

        @Suppress("UNCHECKED_CAST")
        private fun getObjectDescription(value: Any?): String {
            if (value == null) {
                return "null"
            } else if (value is Iterable<*>) {
                val iterValue : Iterable<Any> = value as Iterable<Any>
                return getIterableObjectDescription(iterValue)
            } else {
                return value.toString()
            }
        }

        private fun getIterableObjectDescription(iterValue: Iterable<Any>): String {
            val valueBuilder = StringBuilder("[ ")
            for (element in iterValue) {
                valueBuilder.append(element)
                valueBuilder.append(", ")
            }
            valueBuilder.delete(valueBuilder.length - 2, valueBuilder.length)
            valueBuilder.append(" ]")
            return valueBuilder.toString()
        }
    }
}