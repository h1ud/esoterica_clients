package com.work.esotericaClients.controller;

import com.work.esotericaClients.dto.code.DiscountCodeConsumeRequest;
import com.work.esotericaClients.dto.code.DiscountCodeGenerateRequest;
import com.work.esotericaClients.dto.code.DiscountCodeResponse;
import com.work.esotericaClients.service.DiscountCodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codes")
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;

    public DiscountCodeController(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @PostMapping("/generate")
    public ResponseEntity<DiscountCodeResponse> generate(@Valid @RequestBody DiscountCodeGenerateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountCodeService.generate(request));
    }

    @PostMapping("/consume")
    public ResponseEntity<DiscountCodeResponse> consume(@Valid @RequestBody DiscountCodeConsumeRequest request) {
        return ResponseEntity.ok(discountCodeService.consume(request));
    }
}

