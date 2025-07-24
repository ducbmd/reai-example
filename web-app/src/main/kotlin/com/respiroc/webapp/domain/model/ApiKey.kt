package com.respiroc.webapp.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "api_keys")
open class ApiKey : Serializable {

    companion object {
        const val KEY_ID_LENGTH = 32
        const val SECRET_LENGTH = 32
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = -1

    @Column(name = "tenant_id", nullable = false)
    open var tenantId: Long = -1

    @Column(name = "user_id", nullable = false)
    open var userId: Long = -1

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @Size(max = KEY_ID_LENGTH)
    @Column(name = "key_id", nullable = false, unique = true, length = KEY_ID_LENGTH)
    open lateinit var keyId: String

    @Size(max = 255)
    @Column(name = "hashed_secret", nullable = false)
    open lateinit var hashedSecret: String

    @Size(max = 255)
    @Column(name = "marked_api_key", nullable = false)
    open lateinit var markedApiKey: String

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @Column(name = "last_used_at")
    open var lastUsedAt: Instant? = null
} 