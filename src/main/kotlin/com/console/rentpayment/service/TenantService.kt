package com.console.rentpayment.service

import com.console.rentpayment.dataTransferObject.TenantRequest
import com.console.rentpayment.dataTransferObject.TenantResponse
import com.console.rentpayment.dataTransferObject.TenantSummaryResponse
import com.console.rentpayment.domain.Tenant

/**
 * Created by Nick on 14/11/2016.
 */
interface TenantService {

    fun findTenantById(tenantId : Long) : TenantResponse?

    fun findTenantByIdDomain(tenantId : Long) : Tenant?

    fun doesTenantExist(tenantName : String): Boolean

    fun saveTenant(tenantRequeast : TenantRequest) : TenantResponse

    fun updateTenant(tenant : Tenant, tenantRequeast : TenantRequest) : TenantResponse

    fun deleteTenantById(tenantId : Long)

    fun getTenantsWithRentReceiptsCreateInLastNHours(hours : Long) : List<TenantSummaryResponse>

}


