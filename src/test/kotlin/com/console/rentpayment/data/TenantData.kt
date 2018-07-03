package com.console.rentpayment.data

import com.console.rentpayment.domain.RentReceiptEntity
import com.console.rentpayment.domain.TenantEntity
import com.console.rentpayment.repository.RentReceiptRepository
import com.console.rentpayment.repository.TenantRepository
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Component
class TenantData(
        val tenantRepository: TenantRepository,
        val rentReceiptRepository: RentReceiptRepository
) {

    @Transactional
    fun createTenants(numberToCreate: Int): List<TenantEntity> {
        val tenants: MutableList<TenantEntity> = ArrayList()
        var i = 1
        while (i <= numberToCreate) {
            val tenant = TenantEntity(
                    leaseName = "LeaseName$i",
                    name ="Tenant",
                    weeklyRentAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(500)),
                    rentDatePaidTo = LocalDateTime.now(),
                    rentCreditAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(50)))
            tenants.add(tenant)
            i++
        }
        return tenantRepository.saveAll(tenants)
    }

    @Transactional
    fun createTenantsWithRentReceipts(numberOfTenantsToCreate: Int, numberOfRentReceiptForTenantToCreate: Int): List<TenantEntity> {
        var tenants: MutableList<TenantEntity> = mutableListOf()
        var i: Int = 1
        while (i <= numberOfTenantsToCreate) {
            val tenant = TenantEntity(
                    leaseName = "LeaseName$i",
                    name = "Tenant$i",
                    weeklyRentAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(500)),
                    rentDatePaidTo = LocalDateTime.now(),
                    rentCreditAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(50)))
            tenant.rentReceipts = createRentReceipt(numberOfRentReceiptForTenantToCreate)
            tenants.add(tenant)
            i++
        }
        tenants = tenantRepository.saveAll(tenants)
        return tenants.toList()
    }

    private fun createRentReceipt(numberToCreate: Int): MutableSet<RentReceiptEntity> {
        val rentReceipts: MutableSet<RentReceiptEntity> = linkedSetOf()
        var i: Int = 1
        while (i <= numberToCreate) {
            val rentReceipt: RentReceiptEntity = RentReceiptEntity(
                amount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(50))
            )
            rentReceipts.add(rentReceipt)
            i++
        }
        return rentReceipts
    }

}
