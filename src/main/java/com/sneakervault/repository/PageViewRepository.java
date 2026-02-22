package com.sneakervault.repository;

import com.sneakervault.model.PageView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    long countByViewedAtAfter(LocalDateTime after);

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p WHERE p.viewedAt > :after")
    long countDistinctSessionIdByViewedAtAfter(@Param("after") LocalDateTime after);

    @Query("SELECT p.url AS url, COUNT(p) AS cnt FROM PageView p WHERE p.viewedAt > :after GROUP BY p.url ORDER BY cnt DESC")
    List<Object[]> findTopPages(@Param("after") LocalDateTime after, Pageable pageable);

    @Query("SELECT p.referrer AS referrer, COUNT(p) AS cnt FROM PageView p WHERE p.viewedAt > :after AND p.referrer IS NOT NULL AND p.referrer <> '' GROUP BY p.referrer ORDER BY cnt DESC")
    List<Object[]> findTopReferrers(@Param("after") LocalDateTime after, Pageable pageable);
}
