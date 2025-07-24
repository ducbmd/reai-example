package com.respiroc.webapp.service

import com.respiroc.webapp.domain.model.ApiKey
import com.respiroc.webapp.domain.repository.ApiKeyRepository
import com.respiroc.user.application.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant

@Service
@Transactional
class ApiKeyService(
    private val apiKeyRepository: ApiKeyRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val userService: UserService
) {
    private val secureRandom = SecureRandom()

    data class GeneratedApiKey(
        val apiKey: ApiKey,
        val fullKey: String
    )

    data class ApiKeyWithOwner(
        val apiKey: ApiKey,
        val ownerEmail: String
    )

    fun generateApiKey(userId: Long, tenantId: Long, name: String): GeneratedApiKey {
        val keyId = generateRandomHex(ApiKey.KEY_ID_LENGTH)
        val secret = generateRandomHex(ApiKey.SECRET_LENGTH)
        val fullKey = "ak_$keyId.$secret"
        val hashedSecret = passwordEncoder.encode(secret)
        
        val markedApiKey = if (fullKey.length >= 10) {
            fullKey.substring(0, 6) + "..." + fullKey.substring(fullKey.length - 4)
        } else {
            fullKey.substring(0, minOf(6, fullKey.length)) + "..."
        }

        val apiKey = ApiKey().apply {
            this.tenantId = tenantId
            this.userId = userId
            this.name = name
            this.keyId = keyId
            this.hashedSecret = hashedSecret
            this.markedApiKey = markedApiKey
        }

        val savedApiKey = apiKeyRepository.save(apiKey)
        return GeneratedApiKey(savedApiKey, fullKey)
    }

    fun validateApiKey(fullKey: String): ApiKey? {
        if (!fullKey.startsWith("ak_") || !fullKey.contains(".")) {
            return null
        }

        val parts = fullKey.removePrefix("ak_").split(".", limit = 2)
        if (parts.size != 2 || parts[0].length != ApiKey.KEY_ID_LENGTH || parts[1].length != ApiKey.SECRET_LENGTH) {
            return null
        }

        val keyId = parts[0]
        val secret = parts[1]

        val apiKey = apiKeyRepository.findByKeyId(keyId) ?: return null

        return if (passwordEncoder.matches(secret, apiKey.hashedSecret)) {
            updateLastUsed(apiKey.id)
            apiKey
        } else {
            null
        }
    }

    fun getUserApiKeysWithOwners(userId: Long, tenantId: Long): List<ApiKeyWithOwner> {
        val apiKeys = apiKeyRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(userId, tenantId)
        return apiKeys.map { apiKey ->
            val ownerContext = userService.findByIdAndTenantId(apiKey.userId, apiKey.tenantId)
            val ownerEmail = ownerContext?.email ?: "Unknown User"
            ApiKeyWithOwner(apiKey, ownerEmail)
        }
    }

    fun deleteApiKey(keyId: Long, userId: Long, tenantId: Long): Boolean {
        return apiKeyRepository.deleteByIdAndUserIdAndTenantId(keyId, userId, tenantId) > 0
    }

    private fun updateLastUsed(apiKeyId: Long) {
        apiKeyRepository.findById(apiKeyId).ifPresent { apiKey ->
            apiKey.lastUsedAt = Instant.now()
            apiKeyRepository.save(apiKey)
        }
    }

    private fun generateRandomHex(length: Int): String {
        val bytes = ByteArray(length / 2)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
} 