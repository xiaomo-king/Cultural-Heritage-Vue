package com.jiangyou.service;

import com.jiangyou.model.Heritage;
import com.jiangyou.repository.HeritageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class HeritageService {
    private final HeritageRepository heritageRepository;
    public HeritageService(HeritageRepository heritageRepository) { this.heritageRepository = heritageRepository; }

    public Page<Heritage> getList(String category, int page, int size) {
        if (category != null && !category.isEmpty()) {
            return heritageRepository.findByCategoryAndStatus(category, 1, PageRequest.of(page, size));
        }
        return heritageRepository.findByStatus(1, PageRequest.of(page, size));
    }

    public Heritage getDetail(Long id) {
        return heritageRepository.findById(id).map(h -> {
            h.setViewCount(h.getViewCount() + 1);
            return heritageRepository.save(h);
        }).orElse(null);
    }

    public Page<Heritage> search(String keyword, int page, int size) {
        return heritageRepository.search(keyword, PageRequest.of(page, size));
    }

    public List<Heritage> getNearby(double lat, double lng, int limit) {
        return heritageRepository.findNearby(lat, lng, PageRequest.of(0, limit));
    }

    // ===== 管理端方法 =====

    public Page<Heritage> adminList(String category, int page, int size) {
        if (category != null && !category.isEmpty()) {
            return heritageRepository.findByCategoryOrderByIdAsc(category, PageRequest.of(page, size));
        }
        return heritageRepository.findAllByOrderByIdAsc(PageRequest.of(page, size));
    }

    public List<Heritage> searchByName(String keyword) {
        return heritageRepository.findByNameContaining(keyword);
    }

    @Transactional
    public Heritage create(Map<String, Object> body) {
        Heritage h = new Heritage();
        applyBodyToHeritage(h, body);
        h.setCreatedAt(LocalDateTime.now());
        h.setUpdatedAt(LocalDateTime.now());
        return heritageRepository.save(h);
    }

    @Transactional
    public Heritage update(Long id, Map<String, Object> body) {
        Heritage h = heritageRepository.findById(id).orElse(null);
        if (h == null) return null;
        applyBodyToHeritage(h, body);
        h.setUpdatedAt(LocalDateTime.now());
        return heritageRepository.save(h);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!heritageRepository.existsById(id)) return false;
        heritageRepository.deleteById(id);
        return true;
    }

    private void applyBodyToHeritage(Heritage h, Map<String, Object> body) {
        if (body.get("name") != null) h.setName((String) body.get("name"));
        if (body.get("alias") != null) h.setAlias((String) body.get("alias"));
        if (body.get("category") != null) h.setCategory((String) body.get("category"));
        if (body.get("level") != null) h.setLevel((String) body.get("level"));
        if (body.get("batch") != null) h.setBatch((String) body.get("batch"));
        if (body.get("city") != null) h.setCity((String) body.get("city"));
        if (body.get("county") != null) h.setCounty((String) body.get("county"));
        if (body.get("summary") != null) h.setSummary((String) body.get("summary"));
        if (body.get("description") != null) h.setDescription((String) body.get("description"));
        if (body.get("history") != null) h.setHistory((String) body.get("history"));
        if (body.get("features") != null) h.setFeatures((String) body.get("features"));
        if (body.get("coverImage") != null) h.setCoverImage((String) body.get("coverImage"));
        if (body.get("images") != null) h.setImages((String) body.get("images"));
        if (body.get("tags") != null) h.setTags((String) body.get("tags"));
        if (body.get("travelTips") != null) h.setTravelTips((String) body.get("travelTips"));
        if (body.get("visitHours") != null) h.setVisitHours((String) body.get("visitHours"));
        if (body.get("ticketInfo") != null) h.setTicketInfo((String) body.get("ticketInfo"));
        if (body.get("latitude") != null) h.setLatitude(new java.math.BigDecimal(body.get("latitude").toString()));
        if (body.get("longitude") != null) h.setLongitude(new java.math.BigDecimal(body.get("longitude").toString()));
        if (body.get("status") != null) h.setStatus(((Number) body.get("status")).intValue());
    }
}
