package com.console.rentpayment.controller

import com.console.rentpayment.dataTransferObject.RentReceiptRequest
import com.console.rentpayment.dataTransferObject.RentReceiptResponse
import com.console.rentpayment.dataTransferObject.TenantRequest
import com.console.rentpayment.dataTransferObject.TenantResponse
import com.console.rentpayment.domain.TenantEntity
import com.console.rentpayment.service.RentReceiptService
import com.console.rentpayment.service.TenantService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@ControllerAdvice
@RequestMapping(value = "/api/tenants",
        consumes = [(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")],
        produces = [(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")])
class AccountController(
        val tenantService: TenantService,
        val rentReceiptService: RentReceiptService
) {

    //Retrieve Single Tenant by Id
    @RequestMapping(value = "/{id}", method = [(RequestMethod.GET)])
    fun getTenantById(@PathVariable("id") tenantId: Long): ResponseEntity<TenantResponse> {
        val tenantResponseOptional = tenantService.findTenantById(tenantId)
        return if (tenantResponseOptional.isPresent)
            ResponseEntity(tenantResponseOptional.get(), HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }

    //Create a Tenant
    @RequestMapping(method = [(RequestMethod.POST)])
    fun createTenant(@RequestBody tenantRequest: TenantRequest): ResponseEntity<TenantResponse> {
        return ResponseEntity(tenantService.saveTenant(tenantRequest).get(), HttpStatus.OK)
    }

    //Update a Tenant
    @Transactional
    @RequestMapping(value = "/{id}", method = [(RequestMethod.PUT)])
    fun updateTenant(@PathVariable("id") tenantId: Long, @RequestBody tenantRequest: TenantRequest): ResponseEntity<TenantResponse> {
        val tenantEntityOptional = tenantService.findTenantByIdDomain(tenantId)
        return if (tenantEntityOptional.isPresent)
            ResponseEntity(tenantService.updateTenant(tenantEntityOptional.get(), tenantRequest), HttpStatus.OK)
        else
         ResponseEntity(HttpStatus.NOT_FOUND)
    }

    //delete a Tenant
    @RequestMapping(value = "/{id}", method = [(RequestMethod.DELETE)])
    fun deleteTenant(@PathVariable("id") tenantId: Long): ResponseEntity<TenantResponse> {
        val tenantEntityOptional = tenantService.findTenantByIdDomain(tenantId)
        return if (!tenantEntityOptional.isPresent)
             ResponseEntity(HttpStatus.NOT_FOUND)
        else {
            tenantService.deleteTenantById(tenantId)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }

    //Create a Rent Receipt for the Tenant
    @RequestMapping(value = "/{id}/rentreceipts", method = [(RequestMethod.POST)])
    fun createRentReceipt(@PathVariable("id") tenantId: Long, @RequestBody rentReceiptRequest: RentReceiptRequest): ResponseEntity<RentReceiptResponse> {
        val tenantEntityOptional = tenantService.findTenantByIdDomain(tenantId)
        return if (!tenantEntityOptional.isPresent)
             ResponseEntity(HttpStatus.NOT_FOUND)
        else
            ResponseEntity(rentReceiptService.saveRentReceipt(tenantEntityOptional.get(), rentReceiptRequest), HttpStatus.OK)
    }

    //Get all Rent Receipts for the Tenant
    @RequestMapping(value = "/{id}/rentreceipts", method = [(RequestMethod.GET)])
    open fun getRentReceiptsForTenant(@PathVariable("id") tenantId: Long): ResponseEntity<List<RentReceiptResponse>> {
        val tenantEntityOptional = tenantService.findTenantByIdDomain(tenantId)
        return if (!tenantEntityOptional.isPresent)
             ResponseEntity(HttpStatus.NOT_FOUND)
        else
            ResponseEntity(rentReceiptService.getRentReceipts(tenantId), HttpStatus.OK)
    }
}
