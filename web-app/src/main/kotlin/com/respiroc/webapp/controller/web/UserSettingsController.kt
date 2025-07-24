package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/user-settings")
class UserSettingsController : BaseController() {

    @GetMapping
    fun userSettings(model: Model): String {
        addCommonAttributesForCurrentTenant(model, "User Settings")
        return "user-settings/index"
    }
} 