package com.sneakervault.controller;

import com.sneakervault.model.StoreSettings;
import com.sneakervault.repository.StoreSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        StoreSettings settings = storeSettingsRepository.findById(1L)
                .orElseGet(() -> storeSettingsRepository.save(new StoreSettings()));
        return Map.of("storeLive", Boolean.TRUE.equals(settings.getStoreLive()));
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

        storeSettingsRepository.save(settings);
        return ResponseEntity.ok(Map.of("storeLive", Boolean.TRUE.equals(settings.getStoreLive())));
    }
}
