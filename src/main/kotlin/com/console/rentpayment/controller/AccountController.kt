package com.console.rentpayment.controller

import com.console.rentpayment.dataTransferObject.*
import com.console.rentpayment.domain.Tenant
import com.console.rentpayment.service.RentReceiptService
import com.console.rentpayment.service.TenantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional




/**
 * Created by Nick on 14/11/2016.
 */

@RestController
@ControllerAdvice
@RequestMapping(value = "/api/tenants", consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
open class AccountController {

    @Autowired
    open lateinit var tenantService : TenantService

    @Autowired
    open lateinit var rentReceiptService : RentReceiptService

    //Retrieve Single Tenant by Id
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
   open  fun getTenantById(@PathVariable("id") tenantId : Long) : ResponseEntity<TenantResponse> {
        val tenantResponse : TenantResponse? = tenantService.findTenantById(tenantId)
        if (tenantResponse == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(tenantResponse, HttpStatus.OK)
    }


    //Create a Tenant
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    open fun createTenant(@RequestBody tenantRequest: TenantRequest) : ResponseEntity<TenantResponse> {
      //  if (tenantService.doesTenantExist(tenantRequest.name)) {
      //      return ResponseEntity(HttpStatus.CONFLICT)
      //  }
        return ResponseEntity(tenantService.saveTenant(tenantRequest), HttpStatus.OK)
    }


    //Update a Tenant
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.PUT))
    open fun updateTenant(@PathVariable("id") tenantId : Long,  @RequestBody tenantRequest: TenantRequest) : ResponseEntity<TenantResponse> {
        val tenant : Tenant? = tenantService.findTenantByIdDomain(tenantId)
        if (tenant == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        if (tenant.name != tenantRequest.name) {
            //check if name already in use
            if (tenantService.doesTenantExist(tenantRequest.name)) {
                return ResponseEntity(HttpStatus.CONFLICT)
            }
        }

        return ResponseEntity(tenantService.updateTenant(tenant, tenantRequest), HttpStatus.OK)
    }


    //delete a Tenant
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.DELETE))
    open fun deleteTenant(@PathVariable("id") tenantId : Long) : ResponseEntity<TenantResponse> {
        val tenant : Tenant? = tenantService.findTenantByIdDomain(tenantId)
        if (tenant == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        tenantService.deleteTenantById(tenantId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }


    //Create a Rent Receipt for the tenant
    @Transactional
    @RequestMapping(value = "/{id}/rentreceipts", method = arrayOf(RequestMethod.POST))
    open fun createRentReceipt(@PathVariable("id") tenantId : Long,  @RequestBody rentReceiptRequest: RentReceiptRequest) : ResponseEntity<RentReceiptResponse> {
        val tenant : Tenant? = tenantService.findTenantByIdDomain(tenantId)
        if (tenant == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(rentReceiptService.saveRentReceipt(tenant, rentReceiptRequest), HttpStatus.OK)
    }


    //Get all Rent Receipts for the tenant
    @RequestMapping(value = "/{id}/rentreceipts", method = arrayOf(RequestMethod.GET))
    open fun getRentReceiptsForTenant(@PathVariable("id") tenantId : Long) : ResponseEntity<List<RentReceiptResponse>> {
        val tenant : Tenant? = tenantService.findTenantByIdDomain(tenantId)
        if (tenant == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(rentReceiptService.getRentReceipts(tenantId), HttpStatus.OK)
    }


    //Get all tenants who have a Rent Receipt created in the last N hours
    @RequestMapping(value = "/rentreceipts", method = arrayOf(RequestMethod.GET))
    open fun getTenantsWithRentReceiptsCreatedInLastNHours(@RequestParam("hours") hours : Long) : ResponseEntity<List<TenantSummaryResponse>> {
        return ResponseEntity(tenantService.getTenantsWithRentReceiptsCreateInLastNHours(hours), HttpStatus.OK)
    }




}
