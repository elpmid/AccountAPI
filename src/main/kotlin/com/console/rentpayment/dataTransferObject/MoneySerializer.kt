package com.console.rentpayment.dataTransferObject

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.joda.money.Money

/**
 * Using implementation from https://gist.github.com/stickfigure/b4d2af290407f9af4cce
 */
open class MoneySerializer() : JsonSerializer<Money>() {


    override fun serialize(value: Money, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject()
        run {
            jgen.writeNumberField("amount", value.amountMinorInt)
            jgen.writeStringField("str", value.amount.toString())
            jgen.writeStringField("symbol", value.currencyUnit.symbol)
            jgen.writeStringField("currency", value.currencyUnit.code)
            val pretty = prettyPrintWithCents(value)
            jgen.writeStringField("pretty", pretty)
        }
        jgen.writeEndObject()
    }

    /**
     * Makes a nicely formatted version like $50.00 or $23.99.  Always shows cents.
     */
    private fun prettyPrintWithCents(money: Money): String {
        val bld = StringBuilder(money.currencyUnit.symbol)
        bld.append(money.amount.toPlainString())
        return bld.toString()
    }

}