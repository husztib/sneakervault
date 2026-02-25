package com.sneakervault.controller;

import com.sneakervault.model.DiscountCode;
import com.sneakervault.repository.DiscountCodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discount-codes")
public class DiscountCodeController {

    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCodeController(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    @GetMapping
    public List<DiscountCode> getAll() {
        return discountCodeRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code is required"));
        }

        if (discountCodeRepository.findByCodeIgnoreCase(code.trim()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code already exists"));
        }

        DiscountCode dc = new DiscountCode();
        dc.setCode(code.trim().toUpperCase());
        applyFields(dc, body);

        return ResponseEntity.ok(discountCodeRepository.save(dc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return discountCodeRepository.findById(id).map(dc -> {
            if (body.containsKey("code")) {
                String newCode = ((String) body.get("code")).trim().toUpperCase();
                if (!newCode.equalsIgnoreCase(dc.getCode())) {
                    if (discountCodeRepository.findByCodeIgnoreCase(newCode).isPresent()) {
                        return ResponseEntity.badRequest().body((Object) Map.of("error", "Code already exists"));
                    }
                    dc.setCode(newCode);
                }
            }
            applyFields(dc, body);
            return ResponseEntity.ok((Object) discountCodeRepository.save(dc));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!discountCodeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        discountCodeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "noCode"));
        }

        var opt = discountCodeRepository.findByCodeIgnoreCase(code.trim());
        if (opt.isEmpty()) {
            return ResponseEntity.ok(Map.of("valid", false, "error", "notFound"));
        }

        DiscountCode dc = opt.get();

        if (!Boolean.TRUE.equals(dc.getActive())) {
            return ResponseEntity.ok(Map.of("valid", false, "error", "inactive"));
        }

        if (dc.getExpiresAt() != null && dc.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.ok(Map.of("valid", false, "error", "expired"));
        }

        if (dc.getMaxUses() != null && dc.getUsedCount() != null && dc.getUsedCount() >= dc.getMaxUses()) {
            return ResponseEntity.ok(Map.of("valid", false, "error", "usedUp"));
        }

        int totalHUF = body.get("totalHUF") != null ? ((Number) body.get("totalHUF")).intValue() : 0;
        int totalEUR = body.get("totalEUR") != null ? ((Number) body.get("totalEUR")).intValue() : 0;

        int discountHUF = 0;
        int discountEUR = 0;

        if ("PERCENTAGE".equals(dc.getType()) && dc.getPercentOff() != null) {
            discountHUF = (int) Math.round(totalHUF * dc.getPercentOff() / 100.0);
            discountEUR = (int) Math.round(totalEUR * dc.getPercentOff() / 100.0);
        } else if ("FIXED".equals(dc.getType())) {
            discountHUF = dc.getFixedAmountHUF() != null ? dc.getFixedAmountHUF() : 0;
            discountEUR = dc.getFixedAmountEUR() != null ? dc.getFixedAmountEUR() : 0;
        }

        // Don't exceed total
        discountHUF = Math.min(discountHUF, totalHUF);
        discountEUR = Math.min(discountEUR, totalEUR);

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "type", dc.getType(),
                "percentOff", dc.getPercentOff() != null ? dc.getPercentOff() : 0,
                "discountHUF", discountHUF,
                "discountEUR", discountEUR,
                "finalHUF", totalHUF - discountHUF,
                "finalEUR", totalEUR - discountEUR
        ));
    }

    private void applyFields(DiscountCode dc, Map<String, Object> body) {
        if (body.containsKey("type")) dc.setType((String) body.get("type"));
        if (body.containsKey("percentOff")) dc.setPercentOff(body.get("percentOff") != null ? ((Number) body.get("percentOff")).intValue() : null);
        if (body.containsKey("fixedAmountHUF")) dc.setFixedAmountHUF(body.get("fixedAmountHUF") != null ? ((Number) body.get("fixedAmountHUF")).intValue() : null);
        if (body.containsKey("fixedAmountEUR")) dc.setFixedAmountEUR(body.get("fixedAmountEUR") != null ? ((Number) body.get("fixedAmountEUR")).intValue() : null);
        if (body.containsKey("active")) dc.setActive(body.get("active") != null ? (Boolean) body.get("active") : true);
        if (body.containsKey("maxUses")) dc.setMaxUses(body.get("maxUses") != null ? ((Number) body.get("maxUses")).intValue() : null);
        if (body.containsKey("expiresAt")) {
            Object exp = body.get("expiresAt");
            if (exp != null && !exp.toString().isBlank()) {
                dc.setExpiresAt(LocalDateTime.parse(exp.toString()));
            } else {
                dc.setExpiresAt(null);
            }
        }
    }
}
