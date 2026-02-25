package com.sneakervault.repository;

import com.sneakervault.model.ShoeClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShoeClickRepository extends JpaRepository<ShoeClick, Long> {

    @Query("SELECT c.shoeId, COUNT(c) FROM ShoeClick c GROUP BY c.shoeId")
    List<Object[]> countClicksPerShoe();
}
