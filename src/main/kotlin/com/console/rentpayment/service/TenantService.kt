package com.console.rentpayment.service

import com.console.rentpayment.dataTransferObject.TenantRequest
import com.console.rentpayment.dataTransferObject.TenantResponse
import com.console.rentpayment.dataTransferObject.TenantSummaryResponse
import com.console.rentpayment.domain.TenantEntity
import com.console.rentpayment.repository.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional


interface TenantService {

    fun findTenantById(tenantId: Long): Optional<TenantResponse>

    fun findTenantByIdDomain(tenantId: Long): Optional<TenantEntity>

    fun saveTenant(tenantRequest: TenantRequest): Optional<TenantResponse>

    fun updateTenant(tenant: TenantEntity, tenantRequest: TenantRequest): TenantResponse

    fun deleteTenantById(tenantId: Long)

}

@Service
class TenantServiceImpl(
        val tenantRepository: TenantRepository
) : TenantService {

    var logger = LoggerFactory.getLogger(TenantServiceImpl::class.java)

    //Get TenantEntity by Id
    @Transactional(readOnly = true)
    override fun findTenantById(tenantId: Long): Optional<TenantResponse> {
        return toTenantResponse(tenantRepository.findById(tenantId))
    }

    //Get TenantEntity by Id
    @Transactional(readOnly = true)
    override fun findTenantByIdDomain(tenantId: Long): Optional<TenantEntity> {
        return tenantRepository.findById(tenantId)
    }

    //Save a TenantEntity
    @Transactional
    override fun saveTenant(tenantRequest: TenantRequest): Optional<TenantResponse> {
        var tenantEntity = toTenant(tenantRequest)
        tenantEntity = tenantRepository.save(tenantEntity)
        return toTenantResponse(Optional.of(tenantEntity))
    }

    //Update TenantEntity
    @Transactional
    override fun updateTenant(tenant: TenantEntity, tenantRequest: TenantRequest): TenantResponse {
        tenant.name = tenantRequest.name
        tenant.weeklyRentAmount = tenantRequest.weeklyRentAmount
        tenant.rentCreditAmount = tenantRequest.rentCreditAmount
        tenant.rentDatePaidTo = tenantRequest.rentDatePaidTo

        val tenantUpdated = tenantRepository.save(tenant)
        return toTenantResponse(Optional.of(tenantUpdated)).get()
    }

    //Delete a tenant
    @Transactional
    override fun deleteTenantById(tenantId: Long) = tenantRepository.deleteById(tenantId)

    //Convert a tenant domain object to a TenantSummaryResponse DTO
    private fun toTenantSummaryResponse(tenant: TenantEntity): TenantSummaryResponse {
        return TenantSummaryResponse(id = tenant.id, name = tenant.name)
    }

    //Convert a list of TenantEntity domain objects to a List of TenantResponse DTOs
    private fun toTenantResponseList(tenants: List<TenantEntity>): List<TenantResponse> {
        return tenants.map { toTenantResponse(Optional.of(it)).get()}
    }

    //Convert a tenant domain object to a Optional<TenantResponse> DTO
    private fun toTenantResponse(tenantEntityOptional: Optional<TenantEntity>): Optional<TenantResponse> {
        return if (tenantEntityOptional.isPresent) {
            val tenantEntity = tenantEntityOptional.get()
            Optional.of(
                    TenantResponse(
                            id = tenantEntity.id!!,
                            leaseName = tenantEntity.leaseName,
                            name = tenantEntity.name,
                            weeklyRentAmount = tenantEntity.weeklyRentAmount,
                            rentDatePaidTo = tenantEntity.rentDatePaidTo,
                            rentCreditAmount = tenantEntity.rentCreditAmount,
                            createdBy = tenantEntity.createdBy,
                            createdDate = tenantEntity.createdDate,
                            lastModifiedDate = tenantEntity.lastModifiedDate,
                            lastModifiedBy = tenantEntity.lastModifiedBy
                    )
            )
        } else
            Optional.empty()
    }

    //Convert a TenantRequest DTO to a tenant domain object
    private fun toTenant(tenantRequest: TenantRequest): TenantEntity {
        return TenantEntity(
                leaseName = tenantRequest.leaseName,
                name = tenantRequest.name,
                weeklyRentAmount = tenantRequest.weeklyRentAmount,
                rentDatePaidTo = tenantRequest.rentDatePaidTo,
                rentCreditAmount = tenantRequest.rentCreditAmount
        )
    }

}