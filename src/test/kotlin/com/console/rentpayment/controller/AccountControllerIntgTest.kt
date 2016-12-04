package com.console.rentpayment.controller

import com.console.rentpayment.data.TenantData
import com.console.rentpayment.dataTransferObject.RentReceiptRequest
import com.console.rentpayment.dataTransferObject.TenantRequest
import com.console.rentpayment.domain.Tenant
import com.console.rentpayment.repository.RentReceiptRepository
import com.console.rentpayment.repository.TenantRepository
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.IsEqual.equalTo
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


/**
 * Created by Nick on 15/11/2016.
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntgTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var tenantRepository : TenantRepository

    @Autowired
    private lateinit var rentReceiptRepository : RentReceiptRepository

    @Autowired
    lateinit  var tenantData : TenantData

    @Autowired
    lateinit var objectMapper : ObjectMapper

    @Before
    fun setup() {

    }

    //Test Retrieving a Single Tenant by Id
    @Test
    fun getTenantById_run_returnsTenantResponse() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants : List<Tenant> = tenantData.createTeants(2)
        val tenantOne = tenants.get(0)

        // Act & Assert
        mockMvc.perform(get("/api/tenants/" + tenantOne.id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(tenantOne.id!!.toInt())))
                .andExpect(jsonPath("$.name", equalTo("Tenant1")))
                .andExpect(jsonPath("$.weeklyRentAmount.str", equalTo("500.00")))
                .andExpect(jsonPath("$.rentCreditAmount.str", equalTo("50.00")))
                .andExpect(jsonPath("$.createdBy", equalTo("System")))
                .andExpect(jsonPath("$.lastModifiedBy", equalTo("System")))
                .andExpect(jsonPath("$.lastModifiedDate", equalTo(tenantOne.lastModifiedDate.toString())))
                .andExpect(jsonPath("$.createdDate", equalTo(tenantOne.createdDate.toString())))
    }


    // Test Creating a Tenant
    @Test
    fun createTenant_run_returnsTenantResponse() {
        // Arrange
        tenantRepository.deleteAll()
        var tenantRequest : TenantRequest = TenantRequest(name = "NewTenant", weeklyRentAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(500)),
                                                          rentDatePaidTo = LocalDateTime.now(), rentCreditAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(50)))

        // Act & Assert
        mockMvc.perform(post("/api/tenants/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(tenantRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("NewTenant")))
                .andExpect(jsonPath("$.weeklyRentAmount.str", equalTo("500.00")))
                .andExpect(jsonPath("$.rentCreditAmount.str", equalTo("50.00")))
    }


    //Test Update a Tenant
    @Test
    fun updateTenant_run_returnsTenantResponse() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants: List<Tenant> = tenantData.createTeants(1)
        val tenantOne = tenants.get(0)
        var tenantRequest: TenantRequest = TenantRequest(name = "UpdatedTenant", weeklyRentAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(700)),
                rentDatePaidTo = LocalDateTime.now(), rentCreditAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(70)))

        // Act & Assert
        mockMvc.perform(put("/api/tenants/" + tenantOne.id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(tenantRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("UpdatedTenant")))
                .andExpect(jsonPath("$.weeklyRentAmount.str", equalTo("700.00")))
                .andExpect(jsonPath("$.rentCreditAmount.str", equalTo("70.00")))
    }

    //Test delete a Tenant
    @Test
    fun deleteTenant_run_tenantDeleted() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants: List<Tenant> = tenantData.createTeants(1)
        val tenantOne = tenants.get(0)
        var tenantRequest: TenantRequest = TenantRequest(name = "UpdatedTenant", weeklyRentAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(700)),
                rentDatePaidTo = LocalDateTime.now(), rentCreditAmount = Money.of(CurrencyUnit.of("AUD"), BigDecimal(70)))

        // Act
        mockMvc.perform(delete("/api/tenants/" + tenantOne.id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(tenantRequest)))
                .andDo(print())
                .andExpect(status().isNoContent())

        //Assert
        var tenant : Tenant? = tenantRepository.findOne(tenantOne.id)
        assertThat(tenant, `is`(nullValue()))

    }

    //Test Create a Rent Receipt for the tenant
    //Date Paid To does not increase
    //Rent Credit increases
    //Amount Paid plus credit is less than weekly amount
    @Test
    fun createRentReceipt_amountPaidPlusCreditIsLessThanWeeklyRent_returnsRentResponse() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants: List<Tenant> = tenantData.createTeants(1)
        val tenantOne = tenants.get(0)

        var rentReceiptRequest: RentReceiptRequest = RentReceiptRequest(Money.of(CurrencyUnit.of("AUD"), BigDecimal(70)))

        // Act
        mockMvc.perform(post("/api/tenants/" + tenantOne.id + "/rentreceipts")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(rentReceiptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId", equalTo(tenantOne.id!!.toInt())))
                .andExpect(jsonPath("$.amount.str", equalTo("70.00")))

        var tenant : Tenant = tenantRepository.findOne(tenantOne.id)
        assertThat(tenant.name, `is`("Tenant1"))
        //Rent Paid To Date remains the same
        assertThat(tenant.rentDatePaidTo, `is`(tenantOne.rentDatePaidTo))
        assertThat(tenant.weeklyRentAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(500))))
        //Rent Credit should have increased
        assertThat(tenant.rentCreditAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(120))))

    }

    //Test Create a Rent Receipt for the tenant
    //Date Paid To does increase
    //Rent Credit goes to $0
    //Amount Paid plus credit is equal to the weekly amount
    @Test
    fun createRentReceipt_amountPaidPlusCreditEqualsTheWeeklyRent_returnsRentResponse() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants: List<Tenant> = tenantData.createTeants(1)
        val tenantOne = tenants.get(0)

        var rentReceiptRequest: RentReceiptRequest = RentReceiptRequest(Money.of(CurrencyUnit.of("AUD"), BigDecimal(450)))

        // Act
        mockMvc.perform(post("/api/tenants/" + tenantOne.id + "/rentreceipts")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(rentReceiptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId", equalTo(tenantOne.id!!.toInt())))
                .andExpect(jsonPath("$.amount.str", equalTo("450.00")))

        var tenant : Tenant = tenantRepository.findOne(tenantOne.id)
        assertThat(tenant.name, `is`("Tenant1"))
        //Rent Paid To Date remains the same
        assertThat(tenant.rentDatePaidTo, `is`(tenantOne.rentDatePaidTo.plusWeeks(1)))
        assertThat(tenant.weeklyRentAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(500))))
        //Rent Credit should have increased
        assertThat(tenant.rentCreditAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(0))))

    }

    //Test Create a Rent Receipt for the tenant
    //Date Paid To does increase
    //Rent Credit goes to what was left over
    //Amount Paid plus credit is greater than the weekly amount
    @Test
    fun createRentReceipt_amountPaidPlusCreditGreaterThanTheWeeklyRent_returnsRentResponse() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants: List<Tenant> = tenantData.createTeants(1)
        val tenantOne = tenants.get(0)

        var rentReceiptRequest: RentReceiptRequest = RentReceiptRequest(Money.of(CurrencyUnit.of("AUD"), BigDecimal(600)))

        // Act
        mockMvc.perform(post("/api/tenants/" + tenantOne.id + "/rentreceipts")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(rentReceiptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId", equalTo(tenantOne.id!!.toInt())))
                .andExpect(jsonPath("$.amount.str", equalTo("600.00")))

        var tenant : Tenant = tenantRepository.findOne(tenantOne.id)
        assertThat(tenant.name, `is`("Tenant1"))
        //Rent Paid To Date remains the same
        assertThat(tenant.rentDatePaidTo, `is`(tenantOne.rentDatePaidTo.plusWeeks(1)))
        assertThat(tenant.weeklyRentAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(500))))
        //Rent Credit should have increased
        assertThat(tenant.rentCreditAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(150))))
    }

    @Test
    //Test Get all Rent Receipts for the tenant
    fun getRentReceiptsForTenant_run_retunsListOfRentReceiptResponses() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants : List<Tenant> = tenantData.createTeantsWithRentReceipts(1,4)
        val tenantOne = tenants.get(0)

        var out : MutableList<Int> = ArrayList()
        out.add(1)

        // Act & Assert
        mockMvc.perform(get("/api/tenants/" + tenantOne.id + "/rentreceipts")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize<Any>((4))))
                //TODO add some more checks
    }


    //Test Create a Rent Receipt for the tenant
    //Date Paid To does increase by 3 weeks.
    //Rent Credit goes to what was left over
    //Amount Paid plus credit is greater than 3 weekly rent amounts
    @Test
    fun createRentReceipt_amountPaidPlusCreditGreaterThanThe3WeeklyRents_returnsRentResponse() {
        // Arrange
        tenantRepository.deleteAll()
        val tenants: List<Tenant> = tenantData.createTeants(1)
        val tenantOne = tenants.get(0)

        var rentReceiptRequest: RentReceiptRequest = RentReceiptRequest(Money.of(CurrencyUnit.of("AUD"), BigDecimal(1500)))

        // Act
        mockMvc.perform(post("/api/tenants/" + tenantOne.id + "/rentreceipts")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName())
                .content(convertObjectToJsonBytes(rentReceiptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId", equalTo(tenantOne.id!!.toInt())))
                .andExpect(jsonPath("$.amount.str", equalTo("1500.00")))

        var tenant : Tenant = tenantRepository.findOne(tenantOne.id)
        assertThat(tenant.name, `is`("Tenant1"))
        //Rent Paid To Date increase 3 weeks
        assertThat(tenant.rentDatePaidTo, `is`(tenantOne.rentDatePaidTo.plusWeeks(3)))
        assertThat(tenant.weeklyRentAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(500))))
        //Rent Credit should be 0
        assertThat(tenant.rentCreditAmount, `is`(Money.of(CurrencyUnit.of("AUD"), BigDecimal(50))))
    }


    @Test
    //Test Get all tenants who have a Rent Receipt created in the last N hours
    fun getTenantsWithRentReceiptsCreatedInLastNHours_run_returnsListOfTenantSummaryResponses() {
        // Arrange
        tenantRepository.deleteAll()
        //Create Four Tenants each with one Receipt - The first two will have their
        //Receipt date set to 2 months ago, so only the last two will be returned
        val tenants : List<Tenant> = tenantData.createTeantsWithRentReceipts(4,1)

        setCreatedByToPast(tenants)

        // Act & Assert
        mockMvc.perform(get("/api/tenants/rentreceipts")
                .param("hours", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8.displayName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize<Any>((2))))
                //TODO add some more checks
    }


    private fun setCreatedByToPast(tenants: List<Tenant>) {
        var i: Int = 0
        while (i <= tenants.size - 3) {
            var tenant: Tenant = tenants.get(i)
            tenant.rentReceipts.get(0).createdDate = LocalDateTime.now().minusMonths(2)
            tenantRepository.save(tenant)
            i++
        }
    }

    private fun convertObjectToJsonBytes(`object`: Any): ByteArray {
         objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
         return objectMapper.writeValueAsBytes(`object`)
    }
}