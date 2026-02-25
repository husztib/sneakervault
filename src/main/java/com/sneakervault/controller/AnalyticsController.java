package com.sneakervault.controller;

import com.sneakervault.model.PageView;
import com.sneakervault.model.ShoeClick;
import com.sneakervault.repository.PageViewRepository;
import com.sneakervault.repository.ShoeClickRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final PageViewRepository pageViewRepository;
    private final ShoeClickRepository shoeClickRepository;

    public AnalyticsController(PageViewRepository pageViewRepository, ShoeClickRepository shoeClickRepository) {
        this.pageViewRepository = pageViewRepository;
        this.shoeClickRepository = shoeClickRepository;
    }

    @PostMapping("/track")
    public ResponseEntity<Void> track(@RequestBody Map<String, String> body) {
        PageView pv = new PageView();
        pv.setUrl(body.get("url"));
        pv.setReferrer(body.get("referrer"));
        pv.setUserAgent(body.get("userAgent"));
        pv.setSessionId(body.get("sessionId"));
        pv.setViewedAt(LocalDateTime.now());
        pageViewRepository.save(pv);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.toLocalDate().minusDays(7).atStartOfDay();
        LocalDateTime monthStart = now.toLocalDate().minusDays(30).atStartOfDay();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayViews", pageViewRepository.countByViewedAtAfter(todayStart));
        result.put("todayVisitors", pageViewRepository.countDistinctSessionIdByViewedAtAfter(todayStart));
        result.put("weekViews", pageViewRepository.countByViewedAtAfter(weekStart));
        result.put("weekVisitors", pageViewRepository.countDistinctSessionIdByViewedAtAfter(weekStart));
        result.put("monthViews", pageViewRepository.countByViewedAtAfter(monthStart));
        result.put("monthVisitors", pageViewRepository.countDistinctSessionIdByViewedAtAfter(monthStart));

        List<Map<String, Object>> topPages = new ArrayList<>();
        for (Object[] row : pageViewRepository.findTopPages(monthStart, PageRequest.of(0, 5))) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("url", row[0]);
            entry.put("count", row[1]);
            topPages.add(entry);
        }
        result.put("topPages", topPages);

        List<Map<String, Object>> topReferrers = new ArrayList<>();
        for (Object[] row : pageViewRepository.findTopReferrers(monthStart, PageRequest.of(0, 5))) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("referrer", row[0]);
            entry.put("count", row[1]);
            topReferrers.add(entry);
        }
        result.put("topReferrers", topReferrers);

        return result;
    }

    @PostMapping("/click")
    public ResponseEntity<Void> trackClick(@RequestBody Map<String, Object> body) {
        ShoeClick click = new ShoeClick();
        click.setShoeId(((Number) body.get("shoeId")).longValue());
        click.setSessionId((String) body.get("sessionId"));
        click.setClickedAt(LocalDateTime.now());
        shoeClickRepository.save(click);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clicks")
    public Map<Long, Long> getClickCounts() {
        Map<Long, Long> counts = new LinkedHashMap<>();
        for (Object[] row : shoeClickRepository.countClicksPerShoe()) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }
}
