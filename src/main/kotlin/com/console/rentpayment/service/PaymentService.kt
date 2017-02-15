package com.console.rentpayment.service

import com.console.rentpayment.domain.*
import com.console.rentpayment.domain.LedgerRecordType.CREDIT
import com.console.rentpayment.domain.PaymentType.EFT
import com.console.rentpayment.domain.TransactionType.BANK
import com.console.rentpayment.repository.PaymentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Created by Nick on 15/02/2017.
 */
@Service
open class PaymentService {

    @Autowired
    lateinit var paymentRepository : PaymentRepository


    //Process a Rent Receipt for a Tenant
    fun savePayment()  {

        val transactionDetailEntity = TransactionDetailEntity(sourceReference = "sref", sourceDescription = "sdesc",
                createdByUserId = 1L, details = "det", transactionMethods = "AA")

        val transactionEntity = TransactionEntity(
                transactionType = BANK,
                reference = "REF",
                transactionDate = LocalDate.now(),
                amount = 100L,
                transactionCategory = TransactionCategory.EFT,
                gstAmount = 10L,
                ledgerHolderId = 1L,
                ledgerRecordType = CREDIT,
                ledgerType = LedgerType.BANK,
                transactionDetailEntity = transactionDetailEntity
            )

        transactionDetailEntity.transactionEntity =transactionEntity

        val transactions =  mutableSetOf(transactionEntity)
        val paymentItemEntity = PaymentItemEntity(
                paymentId = 1L,
                invoiceNumber = "INV",
                transactions = transactions
        )


    val paymentDetailEntity= PaymentDetailEntity(bankAccountName = "ANZ")

       val paymentEntity = PaymentEntity(
                paymentAmount = 100L,
                paymentDetailEntity = paymentDetailEntity,
                paymentDate = LocalDate.now(),
                paymentId = 1,
                reference = "REF",
                type = EFT,
                items = mutableSetOf(paymentItemEntity)
        )

        paymentDetailEntity.paymentEntity = paymentEntity

        paymentRepository.save(paymentEntity)
    }
}