package com.console.rentpayment.repository

import com.console.rentpayment.domain.TenantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TenantRepository :  JpaRepository<TenantEntity, Long>, JpaSpecificationExecutor<Long>