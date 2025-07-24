package com.respiroc.webapp.controller.rest

import com.respiroc.webapp.controller.BaseController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
@RequestMapping("/api/test")
class TestApiController : BaseController() {

    @GetMapping("/hello")
    fun hello(): ResponseEntity<Map<String, Any>> {
        val user = user()
        val currentTenant = currentTenant()
        
        return ResponseEntity.ok(mapOf(
            "message" to "Hello, API!",
            "timestamp" to OffsetDateTime.now(),
            "user" to mapOf(
                "id" to user.id,
                "email" to user.email
            ),
            "tenant" to mapOf(
                "id" to currentTenant.id,
                "companyName" to currentTenant.companyName,
                "countryCode" to currentTenant.countryCode
            )
        ))
    }

    @GetMapping("/user-info")
    fun getUserInfo(): ResponseEntity<Map<String, Any>> {
        val user = user()
        val currentTenant = currentTenant()
        
        return ResponseEntity.ok(mapOf(
            "user" to mapOf(
                "id" to user.id,
                "email" to user.email,
                "roles" to user.roles.map { it.code },
                "permissions" to user.roles.flatMap { role -> role.permissions.map { it.code } },
                "tenantRoles" to currentTenant.roles.map { it.code },
                "tenantPermissions" to currentTenant.roles.flatMap { role -> role.permissions.map { it.code } }
            ),
            "tenant" to mapOf(
                "id" to currentTenant.id,
                "companyName" to currentTenant.companyName,
                "countryCode" to currentTenant.countryCode
            )
        ))
    }
} 