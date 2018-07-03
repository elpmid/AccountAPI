package com.console.rentpayment.service

import com.console.rentpayment.dataTransferObject.RentReceiptRequest
import com.console.rentpayment.dataTransferObject.RentReceiptResponse
import com.console.rentpayment.domain.RentReceiptEntity
import com.console.rentpayment.domain.TenantEntity
import com.console.rentpayment.repository.RentReceiptRepository
import com.console.rentpayment.repository.TenantRepository
import org.joda.money.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface RentReceiptService {

    fun saveRentReceipt(tenant : TenantEntity, rentReceiptRequest : RentReceiptRequest) : RentReceiptResponse

    fun getRentReceipts(tenantId : Long) : List<RentReceiptResponse>

}

@Service
open class RentReceiptServiceImpl : RentReceiptService {

    @Autowired
    lateinit var rentReceiptRepository: RentReceiptRepository

    @Autowired
    lateinit var tenantRepository: TenantRepository

    //Process a Rent Receipt for a TenantEntity
    @Transactional
    override fun saveRentReceipt(tenant: TenantEntity, rentReceiptRequest: RentReceiptRequest): RentReceiptResponse {
        return processRentPayment(tenant, rentReceiptRequest)
    }

    private fun processRentPayment(tenant: TenantEntity, rentReceiptRequest: RentReceiptRequest): RentReceiptResponse {
        val rentReceipt = toRentReceipt(rentReceiptRequest)
        val weeklyRentAmount: Money = tenant.weeklyRentAmount
        val receiptAmount: Money = rentReceiptRequest.amount
        val totalPaid: Money = tenant.rentCreditAmount.plus(receiptAmount)

        if (totalPaid.isGreaterThan(weeklyRentAmount) || totalPaid == weeklyRentAmount) {
            var remaining = totalPaid
            while (remaining.isGreaterThan(weeklyRentAmount) || remaining.equals(weeklyRentAmount)) {
                tenant.rentDatePaidTo = tenant.rentDatePaidTo.plusWeeks(1)
                remaining = remaining.minus(weeklyRentAmount)
            }
            tenant.rentCreditAmount = remaining
        } else {
            tenant.rentCreditAmount = tenant.rentCreditAmount + receiptAmount
        }
        tenant.rentReceipts.add(rentReceipt)
        val tenantUpdated = tenantRepository.save(tenant)
        return toRentReceiptResponse(
                tenantUpdated,
                tenantUpdated.rentReceipts.last())
    }

    //Get the Rent Receipts for the tenant
    override fun getRentReceipts(tenantId: Long): List<RentReceiptResponse> {
        val tenantEntityOptional = tenantRepository.findById(tenantId)
        return toRentReceiptResponseList(tenantEntityOptional.get(), tenantEntityOptional.get().rentReceipts.toList())
    }

    //Convert a list of RentReceiptEntity domain objects to a List of RentReceiptReponse DTOs
    private fun toRentReceiptResponseList(
            tenantEntity: TenantEntity,
            rentReceipts: List<RentReceiptEntity>
    ): List<RentReceiptResponse> {
        return rentReceipts.map { toRentReceiptResponse(tenantEntity, it) }
    }

    //Convert a RentReceiptEntity domain object to a RentReceiptResponse DTO
    private fun toRentReceiptResponse(tenant: TenantEntity, rentReceipt: RentReceiptEntity): RentReceiptResponse {
        return RentReceiptResponse(
                id = rentReceipt.id!!,
                tenantId = tenant.id!!,
                amount = rentReceipt.amount,
                createdBy = rentReceipt.createdBy,
                createdDate = rentReceipt.createdDate,
                lastModifiedDate = rentReceipt.lastModifiedDate,
                lastModifiedBy = rentReceipt.lastModifiedBy)
    }


    //Convert a RentReceiptRequest DTO to a RentReceiptEntity domain object
    private fun toRentReceipt(rentRceiptRequest: RentReceiptRequest): RentReceiptEntity {
        return RentReceiptEntity(amount = rentRceiptRequest.amount)
    }

}