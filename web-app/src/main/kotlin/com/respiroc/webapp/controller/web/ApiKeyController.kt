package com.respiroc.webapp.controller.web

import com.respiroc.webapp.service.ApiKeyService
import com.respiroc.webapp.controller.BaseController
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/user-settings/api-keys")
class ApiKeyController(
    private val apiKeyService: ApiKeyService
) : BaseController() {

    @PostMapping
    @ResponseBody
    fun createApiKey(@RequestParam name: String): ResponseEntity<Map<String, Any>> {
        val userId = user().id
        val tenantId = tenantId()

        val generated = apiKeyService.generateApiKey(userId, tenantId, name)

        return ResponseEntity.ok(mapOf(
            "id" to generated.apiKey.id,
            "name" to generated.apiKey.name,
            "key" to generated.fullKey,
            "markedKey" to generated.apiKey.markedApiKey,
            "createdAt" to generated.apiKey.createdAt
        ))
    }

    @GetMapping
    @HxRequest
    fun getUserApiKeysFragment(model: Model): String {
        val userId = user().id
        val tenantId = tenantId()

        val apiKeysWithOwners = apiKeyService.getUserApiKeysWithOwners(userId, tenantId)
        model.addAttribute("apiKeysWithOwners", apiKeysWithOwners)
        return "fragments/api-keys-list"
    }

    @DeleteMapping("/{keyId}")
    @ResponseBody
    fun deleteApiKey(@PathVariable keyId: Long): ResponseEntity<Map<String, String>> {
        val userId = user().id
        val tenantId = tenantId()

        val deleted = apiKeyService.deleteApiKey(keyId, userId, tenantId)

        return if (deleted) {
            ResponseEntity.ok(mapOf("status" to "deleted"))
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 