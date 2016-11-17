package com.console.rentpayment.service

import com.console.rentpayment.dataTransferObject.RentReceiptRequest
import com.console.rentpayment.dataTransferObject.RentReceiptResponse
import com.console.rentpayment.domain.RentReceipt
import com.console.rentpayment.domain.Tenant
import com.console.rentpayment.repository.RentReceiptRepository
import com.console.rentpayment.repository.TenantRepository
import org.joda.money.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Nick on 14/11/2016.
 */

@Service
open class RentReceiptServiceImpl : RentReceiptService {

    @Autowired
    lateinit var rentReceiptRepository : RentReceiptRepository

    @Autowired
    lateinit var tenantRepository : TenantRepository

    //Process a Rent Receipt for a Tenant
    override fun saveRentReceipt(tenant : Tenant, rentReceiptRequest : RentReceiptRequest) : RentReceiptResponse {
        return processRentPayment(tenant, rentReceiptRequest)

    }

    private fun processRentPayment(tenant: Tenant, rentReceiptRequest: RentReceiptRequest) : RentReceiptResponse {
        val rentReceipt = toRentReceipt(rentReceiptRequest)
        val weeklyRentAmount: Money = tenant.weeklyRentAmount
        val receiptAmount : Money = rentReceiptRequest.amount
        val totalPaid: Money = tenant.rentCreditAmount.plus(receiptAmount)

        if (totalPaid.isGreaterThan(weeklyRentAmount) || totalPaid.equals(weeklyRentAmount)) {
            var remaining : Money = totalPaid
            while (remaining.isGreaterThan(weeklyRentAmount) || remaining.equals(weeklyRentAmount)) {
                tenant.rentDatePaidTo = tenant.rentDatePaidTo.plusWeeks(1)
                remaining = remaining.minus(weeklyRentAmount)
            }
            tenant.rentCreditAmount = remaining
        } else {
            tenant.rentCreditAmount = tenant.rentCreditAmount + receiptAmount
        }
        tenant.rentReceipts.add(rentReceipt)
        var tenantUpdated : Tenant = tenantRepository.save(tenant)
        return  toRentReceiptResponse(tenantUpdated, tenantUpdated.rentReceipts.get(tenantUpdated.rentReceipts.size -1))
    }


    //Get the Rent Receipts for the tenant
    override fun getRentReceipts(tenantId : Long) : List<RentReceiptResponse> {
        val tenant : Tenant = tenantRepository.findById(tenantId)
        return toRentReceiptResponseList(tenant, tenant.rentReceipts)
    }


    //Convert a list of RentReceipt domain objects to a List of RentReceiptReponse DTOs
    private fun toRentReceiptResponseList(tenant : Tenant, rentReceipts : List<RentReceipt>) : List<RentReceiptResponse>
            = rentReceipts.map { toRentReceiptResponse(tenant, it) }


    //Convert a tenant domain object to a TenantResponse DTP
    private fun toRentReceiptResponse(tenant: Tenant, rentReceipt :  RentReceipt) : RentReceiptResponse {
        val rentReceiptRequestResponse = RentReceiptResponse(id = rentReceipt.id, tenantId = tenant.id, amount = rentReceipt.amount,
                createdBy = rentReceipt.createdBy, createdDate = rentReceipt.createdDate,
                lastModifiedDate =  rentReceipt.lastModifiedDate, lastModifiedBy = rentReceipt.lastModifiedBy)
        return rentReceiptRequestResponse
    }


    //Convert a Tenant domain onject & RentReceiptRequest DTO to a RentReceipt domain object
    private fun toRentReceipt(rentRceiptRequest : RentReceiptRequest) : RentReceipt {
        val rentReceipt : RentReceipt = RentReceipt(rentRceiptRequest.amount)
        return rentReceipt
    }

}