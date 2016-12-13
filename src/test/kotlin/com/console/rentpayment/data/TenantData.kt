package com.console.rentpayment.data

import com.console.rentpayment.domain.RentReceipt
import com.console.rentpayment.domain.Tenant
import com.console.rentpayment.repository.RentReceiptRepository
import com.console.rentpayment.repository.TenantRepository
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


/**
 * Created by Nick on 14/11/2016.
 */

@Component
open class TenantData {

    @Autowired
    open lateinit var tenantRepository: TenantRepository

    @Autowired
    open lateinit var rentReceiptRepository: RentReceiptRepository

    @Transactional
    fun createTeants(numberToCreate : Int) : List<Tenant> {
        var tenants : MutableList<Tenant> = ArrayList()
        var i : Int = 1
        while (i <= numberToCreate) {
            var tenant =  Tenant("Tenant", Money.of(CurrencyUnit.of("AUD"), BigDecimal(500)), LocalDateTime.now(),
                                 Money.of(CurrencyUnit.of("AUD"), BigDecimal(50)))
            tenants.add(tenant)
            i++
        }
        return tenantRepository.save(tenants)
    }


    @Transactional
    fun createTeantsWithRentReceipts(numberOfTenantsToCreate : Int, numberOfRentReceiptForTenantToCreate: Int) : List<Tenant> {
        var tenants : MutableList<Tenant> = ArrayList()
        var i : Int = 1
        while (i <= numberOfTenantsToCreate) {
            var tenant =  Tenant("Tenant" + i, Money.of(CurrencyUnit.of("AUD"), BigDecimal(500)), LocalDateTime.now(),
                                  Money.of(CurrencyUnit.of("AUD"), BigDecimal(50)))
            tenant.rentReceipts = createRentReceipt(numberOfRentReceiptForTenantToCreate)
            tenants.add(tenant)
            i++
        }
        tenants = tenantRepository.save(tenants)
        return tenants
    }


    private fun createRentReceipt(numberToCreate : Int) : MutableList<RentReceipt> {
        var rentReceipts : MutableList<RentReceipt> = ArrayList()
        var i : Int = 1
        while (i <= numberToCreate) {
            var rentReceipt : RentReceipt = RentReceipt()
            rentReceipt.amount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(50))
            rentReceipts.add(rentReceipt)
            i++
        }
        return rentReceipts
    }

}
