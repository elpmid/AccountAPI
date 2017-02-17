package com.console.rentpayment.repository

import com.console.rentpayment.domain.Tenant
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Created by Nick on 14/11/2016.
 */
@Repository
interface TenantRepository  : JpaRepository<Tenant, Long> {

    fun findByName(name: String): Tenant

  // @EntityGraph(value = "Tenant.tenantRentReceipts", type = EntityGraph.EntityGraphType.LOAD)
   // @Query("SELECT DISTINCT t FROM TENANT t JOIN FETCH t.rentReceipts r WHERE r.createdDate >= :date")
  //  fun getTenantsWithRentReceiptsCreatedAfter(@Param("date") date : LocalDateTime) : List<Tenant>

    @EntityGraph(value = "Tenant.tenantRentReceipts", type = EntityGraph.EntityGraphType.LOAD)
    fun findById(tenantId : Long) : Tenant

}
