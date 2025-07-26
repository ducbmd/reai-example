package com.respiroc.webapp.domain.repository

import com.respiroc.webapp.domain.model.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, Long> {
    fun findByKeyId(keyId: String): ApiKey?

    fun findByUserIdAndTenantIdOrderByCreatedAtDesc(userId: Long, tenantId: Long): List<ApiKey>

    @Modifying
    @Query("DELETE FROM ApiKey a WHERE a.id = :id AND a.userId = :userId AND a.tenantId = :tenantId")
    fun deleteByIdAndUserIdAndTenantId(@Param("id") id: Long, @Param("userId") userId: Long, @Param("tenantId") tenantId: Long): Int
} 