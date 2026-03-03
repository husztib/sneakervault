package com.sneakervault.controller;

import com.sneakervault.model.StoreSettings;
import com.sneakervault.repository.StoreSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class StoreSettingsController {

    private final StoreSettingsRepository storeSettingsRepository;

    public StoreSettingsController(StoreSettingsRepository storeSettingsRepository) {
        this.storeSettingsRepository = storeSettingsRepository;
    }

    @GetMapping
    public Map<String, Object> getSettings() {
        StoreSettings s = storeSettingsRepository.findById(1L)
                .orElseGet(() -> storeSettingsRepository.save(new StoreSettings()));

        Map<String, Object> map = new HashMap<>();
        map.put("storeLive", Boolean.TRUE.equals(s.getStoreLive()));

        map.put("shippingMagyarPostaEnabled", s.getShippingMagyarPostaEnabled() != null ? s.getShippingMagyarPostaEnabled() : true);
        map.put("shippingGlsEnabled", s.getShippingGlsEnabled() != null ? s.getShippingGlsEnabled() : true);
        map.put("shippingDpdEnabled", s.getShippingDpdEnabled() != null ? s.getShippingDpdEnabled() : true);
        map.put("shippingCsomagpontEnabled", s.getShippingCsomagpontEnabled() != null ? s.getShippingCsomagpontEnabled() : true);

        map.put("shippingMagyarPostaHuf", s.getShippingMagyarPostaHuf() != null ? s.getShippingMagyarPostaHuf() : 1490);
        map.put("shippingGlsHuf", s.getShippingGlsHuf() != null ? s.getShippingGlsHuf() : 1990);
        map.put("shippingDpdHuf", s.getShippingDpdHuf() != null ? s.getShippingDpdHuf() : 1990);
        map.put("shippingCsomagpontHuf", s.getShippingCsomagpontHuf() != null ? s.getShippingCsomagpontHuf() : 990);

        map.put("shippingMagyarPostaEur", s.getShippingMagyarPostaEur() != null ? s.getShippingMagyarPostaEur() : 4);
        map.put("shippingGlsEur", s.getShippingGlsEur() != null ? s.getShippingGlsEur() : 5);
        map.put("shippingDpdEur", s.getShippingDpdEur() != null ? s.getShippingDpdEur() : 5);
        map.put("shippingCsomagpontEur", s.getShippingCsomagpontEur() != null ? s.getShippingCsomagpontEur() : 3);

        map.put("internationalShippingEnabled", s.getInternationalShippingEnabled() != null ? s.getInternationalShippingEnabled() : true);

        return map;
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> body) {
        StoreSettings settings = storeSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    StoreSettings s = new StoreSettings();
                    return storeSettingsRepository.save(s);
                });

        if (body.containsKey("storeLive")) {
            settings.setStoreLive((Boolean) body.get("storeLive"));
        }

        if (body.containsKey("shippingMagyarPostaEnabled")) {
            settings.setShippingMagyarPostaEnabled((Boolean) body.get("shippingMagyarPostaEnabled"));
        }
        if (body.containsKey("shippingGlsEnabled")) {
            settings.setShippingGlsEnabled((Boolean) body.get("shippingGlsEnabled"));
        }
        if (body.containsKey("shippingDpdEnabled")) {
            settings.setShippingDpdEnabled((Boolean) body.get("shippingDpdEnabled"));
        }
        if (body.containsKey("shippingCsomagpontEnabled")) {
            settings.setShippingCsomagpontEnabled((Boolean) body.get("shippingCsomagpontEnabled"));
        }

        if (body.containsKey("shippingMagyarPostaHuf")) {
            settings.setShippingMagyarPostaHuf(((Number) body.get("shippingMagyarPostaHuf")).intValue());
        }
        if (body.containsKey("shippingGlsHuf")) {
            settings.setShippingGlsHuf(((Number) body.get("shippingGlsHuf")).intValue());
        }
        if (body.containsKey("shippingDpdHuf")) {
            settings.setShippingDpdHuf(((Number) body.get("shippingDpdHuf")).intValue());
        }
        if (body.containsKey("shippingCsomagpontHuf")) {
            settings.setShippingCsomagpontHuf(((Number) body.get("shippingCsomagpontHuf")).intValue());
        }

        if (body.containsKey("shippingMagyarPostaEur")) {
            settings.setShippingMagyarPostaEur(((Number) body.get("shippingMagyarPostaEur")).intValue());
        }
        if (body.containsKey("shippingGlsEur")) {
            settings.setShippingGlsEur(((Number) body.get("shippingGlsEur")).intValue());
        }
        if (body.containsKey("shippingDpdEur")) {
            settings.setShippingDpdEur(((Number) body.get("shippingDpdEur")).intValue());
        }
        if (body.containsKey("shippingCsomagpontEur")) {
            settings.setShippingCsomagpontEur(((Number) body.get("shippingCsomagpontEur")).intValue());
        }

        if (body.containsKey("internationalShippingEnabled")) {
            settings.setInternationalShippingEnabled((Boolean) body.get("internationalShippingEnabled"));
        }

        storeSettingsRepository.save(settings);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
