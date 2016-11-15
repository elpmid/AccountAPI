package com.console.rentpayment.repository

import com.console.rentpayment.domain.RentReceipt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by Nick on 14/11/2016.
 */

@Repository
interface RentReceiptRepository : JpaRepository<RentReceipt, Long> {
}