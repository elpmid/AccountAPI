package com.console.rentpayment

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.util.Assert
import java.io.Serializable
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.Parameter
import javax.persistence.criteria.*

/**
 * Created by Nick on 5/02/2017.
 */

@NoRepositoryBean
interface SimpleJpaEntityGraphRepository<T, ID: Serializable> :  JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    fun findAll(ids: Iterable<ID>?, entityGraphType: EntityGraphType, entityGraphName: String): List<T>
    fun findAll(specification: Specification<T>, entityGraphType: EntityGraphType, entityGraphName: String) : List<T>
    fun findAll(specification : Specification<T>, pageable: Pageable?, entityGraphType: EntityGraphType, entityGraphName: String) : Page<T>
    fun findAll(specification: Specification<T>, sort: Sort, entityGraphType: EntityGraphType, entityGraphName: String) : List<T>
    fun findOne(specification : Specification<T>, entityGraphType: EntityGraphType, entityGraphName: String) : T
    fun findOne(id: ID, entityGraphType: EntityGraphType, entityGraphName: String): T?

}

@NoRepositoryBean
open class SimpleJpaEntityGraphRepositoryImpl<T, ID : Serializable> constructor(entityInformation: JpaEntityInformation<T, *>,
                                                                    entityManager: EntityManager) : SimpleJpaRepository<T, ID>(entityInformation, entityManager),  SimpleJpaEntityGraphRepository<T, ID> {

    private val entityManager: EntityManager
    private val entityInformation: JpaEntityInformation<T, *>
    init{
        this.entityManager = entityManager
        this.entityInformation = entityInformation
    }


    override fun findAll(ids: Iterable<ID>?, entityGraphType: EntityGraphType, entityGraphName: String): List<T> {
        if (ids != null && ids.iterator().hasNext()) {
            if (!this.entityInformation.hasCompositeId()) {
                val specification1 = ByIdsSpecification(this.entityInformation)
                val query1 = this.getQuery(specification1, null as? Sort?)
                query1.setHint(entityGraphType.key, entityManager.getEntityGraph(entityGraphName))
                return query1.setParameter(specification1.parameter as Parameter<T>, ids as T).resultList
            } else {
                val specification = ArrayList<T>()
                val query = ids.iterator()
                while (query.hasNext()) {
                    specification.add(this.findOne(query.next(), entityGraphType, entityGraphName))
                }
                return specification
            }
        } else {
            return emptyList()
        }
    }



    private class ByIdsSpecification<T>(entityInformation: JpaEntityInformation<T, *>): Specification<T> {
        private val entityInformation:JpaEntityInformation<T, *>
        var parameter: ParameterExpression<java.lang.Iterable<*>>? = null
        init{
            this.entityInformation = entityInformation
        }

        override fun toPredicate(root: Root<T>, query:CriteriaQuery<*>, cb:CriteriaBuilder):Predicate {
            val path = root.get(this.entityInformation.getIdAttribute())
            this.parameter = cb.parameter(java.lang.Iterable::class.java)
            var values : Array<Expression<*>> = arrayOf(this.parameter as Expression<*>)
            return path.`in`(*values )
        }

    }

    override fun findAll(specification: Specification<T>, entityGraphType: EntityGraphType, entityGraphName: String): List<T> {
        val query = getQuery(specification, null as Sort)
        query.setHint(entityGraphType.key, entityManager.getEntityGraph(entityGraphName))
        return query.resultList
    }

    override fun findAll(specification: Specification<T>, pageable: Pageable?, entityGraphType: EntityGraphType, entityGraphName: String): Page<T> {
        val query = getQuery(specification, pageable)
        query.setHint(entityGraphType.key, entityManager.getEntityGraph(entityGraphName))
        return if (pageable == null) PageImpl(query.resultList) else this.readPage(query, pageable, specification)
    }

    override fun findAll(specification: Specification<T>, sort: Sort, entityGraphType: EntityGraphType, entityGraphName: String): List<T> {
        val query = getQuery(specification, sort)
        query.setHint(entityGraphType.key, entityManager.getEntityGraph(entityGraphName))
        return query.resultList
    }


    override fun findOne(specification: Specification<T>, entityGraphType: EntityGraphType, entityGraphName: String): T {
        val query = getQuery(specification, null as Sort)
        query.setHint(entityGraphType.key, entityManager.getEntityGraph(entityGraphName))
        return query.singleResult
    }

    override fun findOne(id:ID, entityGraphType: EntityGraphType, entityGraphName: String): T {
        Assert.notNull(id, "The given id must not be null!")
        val domainType = this.domainClass
        if (this.repositoryMethodMetadata == null) {
            val hintsWithEntityGraph = mapOf<String, Any>(entityGraphType.key to entityManager.getEntityGraph(entityGraphName))
            return this.entityManager.find(domainType, id, hintsWithEntityGraph)
        }
        else {
            val type = this.repositoryMethodMetadata!!.lockModeType
            val hints  = this.queryHints //this comes back as unmodifiable
            val hintsWithEntityGraph = hints + mapOf<String, Any>(entityGraphType.key to entityManager.getEntityGraph(entityGraphName))
            return if (type == null) this.entityManager.find(domainType, id, hintsWithEntityGraph) else this.entityManager.find(domainType, id, type, hintsWithEntityGraph)
        }
    }

}