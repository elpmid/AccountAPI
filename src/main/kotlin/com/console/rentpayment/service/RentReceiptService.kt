package com.console.rentpayment.service

import com.console.rentpayment.dataTransferObject.RentReceiptRequest
import com.console.rentpayment.dataTransferObject.RentReceiptResponse
import com.console.rentpayment.domain.Tenant

/**
 * Created by Nick on 14/11/2016.
 */
interface RentReceiptService {

    fun saveRentReceipt(tenant : Tenant, rentReceiptRequest : RentReceiptRequest) : RentReceiptResponse

    fun getRentReceipts(tenantId : Long) : List<RentReceiptResponse>


}