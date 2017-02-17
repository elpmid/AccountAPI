package com.console.rentpayment.service

import com.console.rentpayment.domain.*
import com.console.rentpayment.domain.LedgerRecordType.CREDIT
import com.console.rentpayment.domain.PaymentType.EFT
import com.console.rentpayment.domain.TransactionCategory.PAYMENT
import com.console.rentpayment.domain.TransactionType.BANK
import com.console.rentpayment.repository.PaymentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.Specifications
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Fetch
import javax.persistence.criteria.FetchParent
import javax.persistence.criteria.From
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.JoinType.INNER
import javax.persistence.criteria.JoinType.LEFT
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.transaction.Transactional
import kotlin.reflect.KProperty1

/**
 * Created by Nick on 15/02/2017.
 */
@Service
open class PaymentService {

    @Autowired
    lateinit var paymentRepository : PaymentRepository

    @PersistenceContext
    @Autowired private lateinit var entityManager: EntityManager

    //Process a Rent Receipt for a Tenant
    fun savePayment() : PaymentEntity {

        val transactionDetailEntity = TransactionDetailEntity(sourceReference = "sref", sourceDescription = "sdesc",
                createdByUserId = 1L, details = "det", transactionMethods = "AA")

        val transactionDetailEntity2 = transactionDetailEntity.copy()

        val transactionDetailEntity3 = transactionDetailEntity.copy()
        val transactionDetailEntity4 = transactionDetailEntity.copy()

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

        val transactionEntity2 = transactionEntity.copy(transactionDetailEntity = transactionDetailEntity2, reference = "2")
        val transactionEntity3 = transactionEntity.copy(transactionDetailEntity = transactionDetailEntity3, transactionCategory = PAYMENT, reference = "3")
        val transactionEntity4 = transactionEntity.copy(transactionDetailEntity = transactionDetailEntity4, transactionCategory = PAYMENT, reference = "4")

        transactionDetailEntity.transactionEntity = transactionEntity
        transactionDetailEntity2.transactionEntity = transactionEntity2
        transactionDetailEntity3.transactionEntity = transactionEntity3
        transactionDetailEntity4.transactionEntity = transactionEntity4

        val transactions =  mutableSetOf(transactionEntity, transactionEntity2)
        val paymentItemEntity = PaymentItemEntity(
                invoiceNumber = "INV",
                transactionEntities = transactions
        )

        val transactions2 =  mutableSetOf(transactionEntity3, transactionEntity4)
        val paymentItemEntity2 = PaymentItemEntity(
                invoiceNumber = "REC",
                transactionEntities = transactions2
        )


    val paymentDetailEntity= PaymentDetailEntity(bankAccountName = "ANZ")

       val paymentEntity = PaymentEntity(
                paymentAmount = 100L,
                paymentDetailEntity = paymentDetailEntity,
                paymentDate = LocalDate.now(),
                reference = "REF",
                type = EFT,
                paymentItemEntities = mutableListOf(paymentItemEntity, paymentItemEntity2)
        )
        paymentDetailEntity.paymentEntity = paymentEntity

        val saved = paymentRepository.save(paymentEntity)

        val paymentEntityGet1 = paymentRepository.findOne(where<PaymentEntity> {
            it.fetch(PaymentEntity::paymentDetailEntity, INNER)
            it.fetch(PaymentEntity::paymentItemEntities, INNER)
            equal(it.get(PaymentEntity::paymentId), saved.paymentId)
        }
        ) ?: throw Exception("Payment Not Found")


        val paymentEntityGet = paymentRepository.findOne(where<PaymentEntity> { root ->
            root.fetch(PaymentEntity::paymentDetailEntity, INNER)
            root.fetch(PaymentEntity::paymentReversedEntity, LEFT)
            root.fetch(PaymentEntity::paymentItemEntities, INNER)
                    .fetch<PaymentItemEntity, TransactionEntity>(PaymentItemEntity::transactionEntities.name, INNER)
                    .fetch<TransactionEntity, TransactionDetailEntity>(TransactionEntity::transactionDetailEntity.name, INNER)
            equal(root.get(PaymentEntity::paymentId), saved.paymentId)

        }
        ) ?: throw Exception("Payment Not Found")


        //            root.fetchCollection<PaymentEntity, PaymentEntity, List<PaymentItemEntity>, PaymentItemEntity>(PaymentEntity::paymentItemEntities, INNER)
//                    .fetch<PaymentItemEntity, TransactionEntity>(PaymentItemEntity::transactionEntities.name, INNER)
//                    .fetch<TransactionEntity, TransactionDetailEntity>(TransactionEntity::transactionDetailEntity.name, INNER)



//        val paymentEntityGet = paymentRepository.findOne(
//                id = saved.paymentId!!,
//                entityGraphType = FETCH,
//                entityGraphName = PaymentEntityGraphs.EAGER_PAYMENT_ITEMS_TRANSACTION_TRANSACTION_DETAIL_PAYMENT_DETAIL_PAYMENT_REVERSED) ?: throw Exception("Payment Not Found")



//        val paymentEntityGet = paymentRepository.findOne(
//                id = saved.paymentId!!,
//                entityGraphType = FETCH,
//                entityGraphName = PaymentEntityGraphs.EAGER_PAYMENT_ITEMS_PAYMENT_DETAIL) ?: throw Exception("Payment Not Found")




        entityManager.detach(paymentEntityGet1)
      //  println(paymentEntityGet1)
        return paymentEntityGet1
    }
}

fun <Z, T, R> FetchParent<Z, T>.fetch(prop: KProperty1<T, R?>, joinType: JoinType = JoinType.INNER): Fetch<T, R> = this.fetch(prop.name, joinType)
fun <Z, T, R : Collection<E>, E> FetchParent<Z, T>.fetchCollection(prop: KProperty1<T, R?>, joinType: JoinType = JoinType.INNER): Fetch<T, E> = this.fetch(prop.name, joinType)

//fun <Z, T, R> From<Z, T>.fetch(prop: KProperty1<T, R?>, joinType: JoinType = JoinType.INNER): Fetch<T, R> = this.fetch<T, R>(prop.name, joinType)

// TODO can actually cast Fetch to From in Hibernate, but this seems really dodgy

//fun <Z, T, R> FetchParent<Z, T>.fetch(prop: KProperty1<T, R?>, joinType: JoinType = JoinType.INNER): From<T, R> = this.fetch<T, R>(prop.name, joinType) as From<T, R>



// Helper to enable get by Property

fun <R> Path<*>.get(prop: KProperty1<*, R?>): Path<R> = this.get<R>(prop.name)



// Version of Specifications.where that makes the CriteriaBuilder implicit

fun <T> where(applyQuery: (CriteriaQuery<*>.() -> Unit)? = null, makePredicate: CriteriaBuilder.(Root<T>) -> Predicate): Specifications<T> =

        Specifications.where<T> { root, criteriaQuery, criteriaBuilder ->

            applyQuery?.let { criteriaQuery.applyQuery() }

            criteriaBuilder.makePredicate(root)

        }