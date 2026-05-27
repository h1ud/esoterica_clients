package com.work.esotericaClients.service;

import com.work.esotericaClients.dto.discount.DiscountCreateRequest;
import com.work.esotericaClients.dto.discount.DiscountResponse;
import com.work.esotericaClients.dto.discount.DiscountUpdateRequest;
import com.work.esotericaClients.entity.Discount;
import com.work.esotericaClients.exception.BadRequestException;
import com.work.esotericaClients.exception.ResourceNotFoundException;
import com.work.esotericaClients.repository.DiscountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Transactional
    public DiscountResponse create(DiscountCreateRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());

        Discount discount = Discount.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .stockLimit(request.getStockLimit())
                .usedCount(0)
                .isActive(true)
                .build();

        return toResponse(discountRepository.save(discount));
    }

    @Transactional(readOnly = true)
    public Page<DiscountResponse> findAll(Pageable pageable) {
        return discountRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public DiscountResponse update(Long id, DiscountUpdateRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found"));

        if (discount.getUsedCount() > request.getStockLimit()) {
            throw new BadRequestException("Stock limit cannot be lower than used count");
        }

        discount.setTitle(request.getTitle());
        discount.setDescription(request.getDescription());
        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setStockLimit(request.getStockLimit());
        discount.setIsActive(request.getIsActive());

        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public void delete(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        discount.setIsActive(false);
        discountRepository.save(discount);
    }

    private void validateDates(java.time.LocalDate start, java.time.LocalDate end) {
        if (end.isBefore(start)) {
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    private DiscountResponse toResponse(Discount discount) {
        return DiscountResponse.builder()
                .id(discount.getId())
                .title(discount.getTitle())
                .description(discount.getDescription())
                .discountType(discount.getDiscountType())
                .discountValue(discount.getDiscountValue())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .stockLimit(discount.getStockLimit())
                .usedCount(discount.getUsedCount())
                .isActive(discount.getIsActive())
                .createdAt(discount.getCreatedAt())
                .build();
    }
}

