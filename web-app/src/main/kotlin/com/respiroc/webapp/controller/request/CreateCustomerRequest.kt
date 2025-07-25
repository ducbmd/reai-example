package com.respiroc.webapp.controller.request

import com.respiroc.common.payload.NewContactPayload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateCustomerRequest(

    @field:NotBlank(message = "Contact name is required")
    val name: String,

    @field:Size(max = 36, message = "Organization number must not exceed 36 characters")
    val organizationNumber: String?,

    @field:NotBlank(message = "Private Contact is required")
    val privateContact: Boolean,

    val countryCode: String? = "",
    val city: String? = "",
    val postalCode: String? = "",
    val administrativeDivisionCode: String? = "",
    val addressPart1: String? = "",
    val addressPart2: String? = "",
)

fun CreateCustomerRequest.toPayload(): NewContactPayload {
    return NewContactPayload(
        name = this.name,
        organizationNumber = this.organizationNumber,
        privateContact = this.privateContact,
        countryCode = this.countryCode,
        postalCode = this.postalCode,
        addressPart1 = this.addressPart1,
        addressPart2 = this.addressPart2,
        city = this.city,
        administrativeDivisionCode = this.administrativeDivisionCode
    )
}
