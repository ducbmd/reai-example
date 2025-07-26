package com.respiroc.webapp.security

import com.respiroc.webapp.service.ApiKeyService
import com.respiroc.user.application.UserService
import com.respiroc.util.context.SpringUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthenticationFilter(
    private val apiKeyService: ApiKeyService,
    private val userService: UserService
) : OncePerRequestFilter() {

    companion object {
        private const val GENERIC_AUTH_ERROR = "Authentication failed"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader("X-API-KEY")

        if (apiKey != null && request.requestURI.startsWith("/api/")) {
            val validatedKey = apiKeyService.validateApiKey(apiKey)
            if (validatedKey != null) {
                val userContext = userService.findByIdAndTenantId(validatedKey.userId, validatedKey.tenantId)
                
                if (userContext != null) {
                    val principal = SpringUser(userContext)
                    val authentication = UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.authorities
                    )
                    
                    authentication.details = mapOf(
                        "apiKeyId" to validatedKey.id,
                        "authenticationType" to "API_KEY"
                    )
                    
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, GENERIC_AUTH_ERROR)
                    return
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, GENERIC_AUTH_ERROR)
                return
            }
        }

        filterChain.doFilter(request, response)
    }
} 