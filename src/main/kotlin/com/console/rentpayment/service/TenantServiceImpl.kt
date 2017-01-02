package com.console.rentpayment.service

import com.console.rentpayment.dataTransferObject.TenantRequest
import com.console.rentpayment.dataTransferObject.TenantResponse
import com.console.rentpayment.dataTransferObject.TenantSummaryResponse
import com.console.rentpayment.domain.Tenant
import com.console.rentpayment.logging.Level
import com.console.rentpayment.logging.LevelToProperties
import com.console.rentpayment.logging.Loggable

import com.console.rentpayment.repository.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Created by Nick on 14/11/2016.
 */

@Service
open class TenantServiceImpl : TenantService {

    var logger = LoggerFactory.getLogger(TenantServiceImpl::class.java)
    @Autowired
    lateinit var tenantRepository : TenantRepository


    //Get Tenant by Id
    override fun findTenantById(tenantId : Long) : TenantResponse? {
        var tenant : Tenant? = tenantRepository.findOne(tenantId)
        if (tenant == null) {
            return null
        }
        return toTenantResponse(tenant)
    }


    //Get Tenant by Id
    override fun findTenantByIdDomain(tenantId : Long) : Tenant? {
        return tenantRepository.findOne(tenantId)
    }


    //Check if Tenant exists
    override fun doesTenantExist(tenantName : String): Boolean {
        return tenantRepository.findByName(tenantName) != null
    }


    //Save a Tenant
   // @Loggable
    override fun saveTenant(tenantRequeast : TenantRequest) : TenantResponse {
        var tenant : Tenant = toTenant(tenantRequeast)
        tenant = tenantRepository.save(tenant)
        return toTenantResponse(tenant)
    }


    //Update Tenant
    @Loggable(levelsToProperties = arrayOf(LevelToProperties(level = Level.WARN, properties =  arrayOf("tenant.name")), LevelToProperties(level = Level.ERROR, properties =  arrayOf("tenant.name"))))
    override fun updateTenant(tenant : Tenant, tenantRequeast : TenantRequest) : TenantResponse {
        tenant.name = tenantRequeast.name
        tenant.weeklyRentAmount = tenantRequeast.weeklyRentAmount
        tenant.rentCreditAmount = tenantRequeast.rentCreditAmount
        tenant.rentDatePaidTo = tenantRequeast.rentDatePaidTo

        var tenantCopy : Tenant = Tenant()
        tenantCopy.id = 1
        tenantCopy.name = "Copied Name"
        tenantCopy.weeklyRentAmount = tenantRequeast.weeklyRentAmount
        tenantCopy.rentCreditAmount = tenantRequeast.rentCreditAmount
        tenantCopy.rentDatePaidTo = tenantRequeast.rentDatePaidTo

        val tenantUpdated : Tenant = tenantRepository.save(tenantCopy)
        return toTenantResponse(tenantUpdated)
    }


    //Delete a tenant
    override fun deleteTenantById(tenantId : Long) = tenantRepository.delete(tenantId)


    //List all Tenants which have a Rent Receipt that was created within the last “N” hours
    override fun getTenantsWithRentReceiptsCreateInLastNHours(hours : Long) : List<TenantSummaryResponse> {
        val date : LocalDateTime = LocalDateTime.now().minusHours(hours)
        val tenants : List<Tenant> = tenantRepository.getTenantsWithRentReceiptsCreatedAfter(date)
        return toTenantSummaryResponse(tenants)
    }


    //Convert a list of Tenant domain objects to a List of TenantSummaryReponse DTOs
    private fun toTenantSummaryResponse(tenants : List<Tenant>) : List<TenantSummaryResponse>  = tenants.map { toTenantSummaryResponse(it) }


    //Convert a tenant domain object to a TenantResponse DTO
    private fun toTenantSummaryResponse(tenant :  Tenant) : TenantSummaryResponse {
        val tenantSummaryResponse = TenantSummaryResponse(id = tenant.id, name = tenant.name)
        return tenantSummaryResponse
    }


    //Convert a list of Tenant domain objects to a List of TenantReponse DTOs
    private fun toTenantResponseList(tenants : List<Tenant>) : List<TenantResponse> = tenants.map { toTenantResponse(it) }


    //Convert a tenant domain object to a TenantResponse DTP
    private fun toTenantResponse(tenant :  Tenant) : TenantResponse {
        val tenantResponse = TenantResponse(id = tenant.id, name = tenant.name, weeklyRentAmount =  tenant.weeklyRentAmount,
                                            rentDatePaidTo = tenant.rentDatePaidTo,rentCreditAmount = tenant.rentCreditAmount,
                                            createdBy = tenant.createdBy, createdDate = tenant.createdDate,
                                            lastModifiedDate =  tenant.lastModifiedDate, lastModifiedBy = tenant.lastModifiedBy)
        return tenantResponse
    }


    //Convert a TenantRequest DTO to a tenant domain object
    private fun toTenant(tenantRequest : TenantRequest) : Tenant {
        val tenant = Tenant(tenantRequest.name, weeklyRentAmount =  tenantRequest.weeklyRentAmount,
                            rentDatePaidTo = tenantRequest.rentDatePaidTo, rentCreditAmount = tenantRequest.rentCreditAmount)
        return tenant
    }

}