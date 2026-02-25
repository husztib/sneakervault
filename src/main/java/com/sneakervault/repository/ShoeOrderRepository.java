package com.sneakervault.repository;

import com.sneakervault.model.ShoeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoeOrderRepository extends JpaRepository<ShoeOrder, Long> {
    List<ShoeOrder> findByCustomerEmailOrderByOrderDateDesc(String email);
}
