package com.console.rentpayment.dataTransferObject

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.joda.money.CurrencyUnit
import org.joda.money.Money



/**
 * Created by Nick on 15/11/2016.
 */
open class MoneyDeserializer() : StdDeserializer<Money>(Money::class.java) {

    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Money {
        val moneyTree : JsonNode = jp.readValueAsTree()
        val amount = moneyTree.get("amount").asInt()
        val currencyNode = moneyTree.get("currency")
        val currency = if (currencyNode == null) CurrencyUnit.USD else CurrencyUnit.of(currencyNode!!.asText())
        return Money.ofMinor(currency, amount.toLong())
    }

}