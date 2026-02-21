package com.sneakervault.controller;

import com.sneakervault.model.Shoe;
import com.sneakervault.model.ShoeImage;
import com.sneakervault.repository.ShoeImageRepository;
import com.sneakervault.repository.ShoeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shoes/{shoeId}/images")
public class ShoeImageController {

    private final ShoeRepository shoeRepository;
    private final ShoeImageRepository shoeImageRepository;
    private final Path uploadPath;

    public ShoeImageController(ShoeRepository shoeRepository,
                               ShoeImageRepository shoeImageRepository,
                               @Value("${sneakervault.upload-dir}") String uploadDir) {
        this.shoeRepository = shoeRepository;
        this.shoeImageRepository = shoeImageRepository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<ShoeImage>> getImages(@PathVariable Long shoeId) {
        if (!shoeRepository.existsById(shoeId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shoeImageRepository.findByShoeIdOrderByDisplayOrderAsc(shoeId));
    }

    @PostMapping
    public ResponseEntity<ShoeImage> uploadImage(@PathVariable Long shoeId,
                                                  @RequestParam("file") MultipartFile file) throws IOException {
        return shoeRepository.findById(shoeId).map(shoe -> {
            try {
                String originalFilename = file.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = shoeId + "_" + UUID.randomUUID() + ext;
                Path target = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), target);

                int nextOrder = shoe.getImages().stream()
                        .mapToInt(img -> img.getDisplayOrder() != null ? img.getDisplayOrder() : 0)
                        .max().orElse(-1) + 1;

                boolean isPrimary = shoe.getImages().isEmpty();

                ShoeImage image = new ShoeImage(shoe, "/uploads/" + filename, nextOrder, isPrimary);
                shoe.getImages().add(image);
                shoeRepository.save(shoe);
                syncPrimaryImageUrl(shoe);

                return ResponseEntity.ok(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{imgId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long shoeId, @PathVariable Long imgId) {
        return shoeRepository.findById(shoeId).map(shoe -> {
            ShoeImage target = shoe.getImages().stream()
                    .filter(img -> img.getId().equals(imgId))
                    .findFirst().orElse(null);
            if (target == null) return ResponseEntity.notFound().<Void>build();

            boolean wasPrimary = Boolean.TRUE.equals(target.getPrimaryImage());

            // Delete file if it's in uploads
            String url = target.getImageUrl();
            if (url != null && url.startsWith("/uploads/")) {
                try {
                    Path filePath = uploadPath.resolve(url.substring("/uploads/".length()));
                    Files.deleteIfExists(filePath);
                } catch (IOException ignored) {}
            }

            // Remove from collection — orphanRemoval handles the DB delete
            shoe.getImages().remove(target);

            if (wasPrimary && !shoe.getImages().isEmpty()) {
                shoe.getImages().get(0).setPrimaryImage(true);
            }

            shoeRepository.save(shoe);
            syncPrimaryImageUrl(shoe);

            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{imgId}/primary")
    public ResponseEntity<ShoeImage> setPrimary(@PathVariable Long shoeId, @PathVariable Long imgId) {
        return shoeRepository.findById(shoeId).map(shoe -> {
            ShoeImage target = null;
            for (ShoeImage img : shoe.getImages()) {
                if (img.getId().equals(imgId)) {
                    img.setPrimaryImage(true);
                    target = img;
                } else {
                    img.setPrimaryImage(false);
                }
            }
            if (target == null) return ResponseEntity.notFound().<ShoeImage>build();

            shoeRepository.save(shoe);
            syncPrimaryImageUrl(shoe);

            return ResponseEntity.ok(target);
        }).orElse(ResponseEntity.notFound().build());
    }

    private void syncPrimaryImageUrl(Shoe shoe) {
        // Re-fetch to get updated images
        Shoe fresh = shoeRepository.findById(shoe.getId()).orElse(shoe);
        List<ShoeImage> images = shoeImageRepository.findByShoeIdOrderByDisplayOrderAsc(fresh.getId());
        String primaryUrl = images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getPrimaryImage()))
                .map(ShoeImage::getImageUrl)
                .findFirst()
                .orElse(fresh.getImageUrl());
        fresh.setImageUrl(primaryUrl);
        shoeRepository.save(fresh);
    }
}
