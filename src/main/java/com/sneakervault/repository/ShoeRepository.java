package com.sneakervault.repository;

import com.sneakervault.model.Shoe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    List<Shoe> findByBrand(String brand);
    List<Shoe> findByType(String type);
    List<Shoe> findByGender(String gender);
    List<Shoe> findBySizeEUR(Double sizeEUR);
}
