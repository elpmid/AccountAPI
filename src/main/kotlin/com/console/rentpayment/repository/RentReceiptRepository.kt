package com.console.rentpayment.repository

import com.console.rentpayment.domain.RentReceiptEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface RentReceiptRepository : JpaRepository<RentReceiptEntity, Long>, JpaSpecificationExecutor<Long>