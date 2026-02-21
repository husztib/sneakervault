package com.sneakervault.repository;

import com.sneakervault.model.ShoeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoeImageRepository extends JpaRepository<ShoeImage, Long> {
    List<ShoeImage> findByShoeIdOrderByDisplayOrderAsc(Long shoeId);
}
