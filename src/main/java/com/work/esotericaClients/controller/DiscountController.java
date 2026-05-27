package com.work.esotericaClients.controller;

import com.work.esotericaClients.dto.discount.DiscountCreateRequest;
import com.work.esotericaClients.dto.discount.DiscountResponse;
import com.work.esotericaClients.dto.discount.DiscountUpdateRequest;
import com.work.esotericaClients.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    public ResponseEntity<DiscountResponse> create(@Valid @RequestBody DiscountCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<DiscountResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(discountService.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DiscountUpdateRequest request) {
        return ResponseEntity.ok(discountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        discountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

