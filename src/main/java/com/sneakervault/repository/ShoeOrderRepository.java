package com.sneakervault.repository;

import com.sneakervault.model.ShoeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoeOrderRepository extends JpaRepository<ShoeOrder, Long> {
}
