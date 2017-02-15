/*
 * Copyright 2016 Onthehouse Holdings Pty Ltd. All rights reserved. Not to be copied, redistributed
 * or modified without prior written consent of Onthehouse Holdings Pty Ltd.
 */

package com.console.rentpayment.repository

import com.console.rentpayment.SimpleJpaEntityGraphRepository
import com.console.rentpayment.domain.PaymentEntity
import org.springframework.stereotype.Repository

/**
 * @author Alan Loi (alan.loi@console.com.au)
 */
@Repository
interface PaymentRepository : SimpleJpaEntityGraphRepository<PaymentEntity, Long>

