package com.sneakervault.repository;

import com.sneakervault.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByCustomerIdOrderByLoginAtDesc(Long customerId);
}
