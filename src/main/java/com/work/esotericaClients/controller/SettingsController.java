package com.work.esotericaClients.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.auth.AdminAuthorizationService;
import com.work.esotericaClients.dto.VipSettingsRequest;
import com.work.esotericaClients.dto.VipSettingsResponse;
import com.work.esotericaClients.service.StoreSettingsService;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final StoreSettingsService settingsService;
    private final AdminAuthorizationService adminAuthorizationService;

    public SettingsController(
        StoreSettingsService settingsService,
        AdminAuthorizationService adminAuthorizationService
    ) {
        this.settingsService = settingsService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @GetMapping("/vip")
    public VipSettingsResponse getVipSettings() {
        return settingsService.getVipSettings();
    }

    @PutMapping("/vip")
    public VipSettingsResponse updateVipSettings(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody VipSettingsRequest request
    ) {
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return settingsService.updateVipRequiredProducts(request.vipRequiredProducts());
    }
}
