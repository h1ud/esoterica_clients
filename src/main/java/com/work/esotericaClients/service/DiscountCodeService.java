package com.work.esotericaClients.service;

import com.work.esotericaClients.dto.code.DiscountCodeConsumeRequest;
import com.work.esotericaClients.dto.code.DiscountCodeGenerateRequest;
import com.work.esotericaClients.dto.code.DiscountCodeResponse;
import com.work.esotericaClients.entity.AdminUser;
import com.work.esotericaClients.entity.Client;
import com.work.esotericaClients.entity.Discount;
import com.work.esotericaClients.entity.DiscountCode;
import com.work.esotericaClients.entity.DiscountCodeStatus;
import com.work.esotericaClients.exception.BadRequestException;
import com.work.esotericaClients.exception.ResourceNotFoundException;
import com.work.esotericaClients.repository.AdminUserRepository;
import com.work.esotericaClients.repository.ClientRepository;
import com.work.esotericaClients.repository.DiscountCodeRepository;
import com.work.esotericaClients.repository.DiscountRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscountCodeService {

    private final DiscountCodeRepository codeRepository;
    private final ClientRepository clientRepository;
    private final DiscountRepository discountRepository;
    private final AdminUserRepository adminUserRepository;

    public DiscountCodeService(
            DiscountCodeRepository codeRepository,
            ClientRepository clientRepository,
            DiscountRepository discountRepository,
            AdminUserRepository adminUserRepository) {
        this.codeRepository = codeRepository;
        this.clientRepository = clientRepository;
        this.discountRepository = discountRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Transactional
    public DiscountCodeResponse generate(DiscountCodeGenerateRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Discount discount = discountRepository.findById(request.getDiscountId())
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found"));

        LocalDateTime now = LocalDateTime.now();
        if (!Boolean.TRUE.equals(discount.getIsActive())) {
            throw new BadRequestException("Discount is not active");
        }
        if (discount.getEndDate().isBefore(now.toLocalDate())) {
            throw new BadRequestException("Discount is expired");
        }
        if (discount.getUsedCount() >= discount.getStockLimit()) {
            throw new BadRequestException("Discount stock limit reached");
        }

        String code = generateUniqueCode();
        DiscountCode discountCode = DiscountCode.builder()
                .code(code)
                .generatedAt(now)
                .expiresAt(discount.getEndDate().atTime(23, 59, 59))
                .status(DiscountCodeStatus.GENERATED)
                .client(client)
                .discount(discount)
                .build();

        discount.setUsedCount(discount.getUsedCount() + 1);
        DiscountCode saved = codeRepository.save(discountCode);
        return toResponse(saved);
    }

    @Transactional
    public DiscountCodeResponse consume(DiscountCodeConsumeRequest request) {
        DiscountCode discountCode = codeRepository.findByCode(request.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Code not found"));

        if (discountCode.getStatus() != DiscountCodeStatus.GENERATED) {
            throw new BadRequestException("Code is not available");
        }

        LocalDateTime now = LocalDateTime.now();
        if (discountCode.getExpiresAt().isBefore(now)) {
            discountCode.setStatus(DiscountCodeStatus.EXPIRED);
            codeRepository.save(discountCode);
            throw new BadRequestException("Code is expired");
        }

        AdminUser admin = adminUserRepository.findByUsername(request.getAdminUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        discountCode.setStatus(DiscountCodeStatus.CONSUMED);
        discountCode.setUsedAt(now);
        discountCode.setConsumedByAdmin(admin);

        DiscountCode saved = codeRepository.save(discountCode);
        return toResponse(saved);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (codeRepository.findByCode(code).isPresent());
        return code;
    }

    private DiscountCodeResponse toResponse(DiscountCode discountCode) {
        return DiscountCodeResponse.builder()
                .id(discountCode.getId())
                .code(discountCode.getCode())
                .generatedAt(discountCode.getGeneratedAt())
                .expiresAt(discountCode.getExpiresAt())
                .status(discountCode.getStatus())
                .usedAt(discountCode.getUsedAt())
                .clientId(discountCode.getClient().getId())
                .discountId(discountCode.getDiscount().getId())
                .build();
    }
}

