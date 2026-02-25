package com.sneakervault.repository;

import com.sneakervault.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByCustomerIdOrderByLoginAtDesc(Long customerId);
    @Transactional
    void deleteByCustomerId(Long customerId);
}
