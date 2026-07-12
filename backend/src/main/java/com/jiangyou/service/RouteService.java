package com.jiangyou.service;

import com.jiangyou.model.Route;
import com.jiangyou.model.RoutePoint;
import com.jiangyou.repository.RouteRepository;
import com.jiangyou.repository.RoutePointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;
    public RouteService(RouteRepository rr, RoutePointRepository rpr) { this.routeRepository = rr; this.routePointRepository = rpr; }

    public List<Route> getList(String city) {
        if (city != null && !city.isEmpty()) return routeRepository.findByCityAndStatus(city, 1);
        return routeRepository.findByStatus(1);
    }

    public Map<String, Object> getDetail(Long routeId) {
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) return null;
        List<RoutePoint> points = routePointRepository.findByRouteIdOrderByPointOrderAsc(routeId);
        Map<String, Object> result = new HashMap<>();
        result.put("route", route);
        result.put("points", points);
        return result;
    }

    // ===== 管理端方法 =====
    public List<Route> adminList() { return routeRepository.findAllByOrderByCreatedAtDesc(); }

    @Transactional
    public Route create(Map<String, Object> body) {
        Route r = new Route();
        applyBodyToRoute(r, body);
        return routeRepository.save(r);
    }

    @Transactional
    public Route update(Long id, Map<String, Object> body) {
        Route r = routeRepository.findById(id).orElse(null);
        if (r == null) return null;
        applyBodyToRoute(r, body);
        return routeRepository.save(r);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!routeRepository.existsById(id)) return false;
        // 先删除关联节点
        List<RoutePoint> points = routePointRepository.findByRouteIdOrderByPointOrderAsc(id);
        routePointRepository.deleteAll(points);
        // 再删除路线
        routeRepository.deleteById(id);
        return true;
    }

    public void savePoints(Long routeId, List<Map<String, Object>> points) {
        // 先删除旧的
        List<RoutePoint> oldPoints = routePointRepository.findByRouteIdOrderByPointOrderAsc(routeId);
        routePointRepository.deleteAll(oldPoints);
        // 保存新的
        for (int i = 0; i < points.size(); i++) {
            Map<String, Object> p = points.get(i);
            RoutePoint rp = new RoutePoint();
            rp.setRouteId(routeId);
            rp.setHeritageId(((Number) p.get("heritageId")).longValue());
            rp.setPointOrder(i);
            rp.setStayTime(p.get("stayTime") != null ? (String) p.get("stayTime") : "");
            rp.setNote(p.get("note") != null ? (String) p.get("note") : "");
            routePointRepository.save(rp);
        }
    }

    private void applyBodyToRoute(Route r, Map<String, Object> body) {
        if (body.get("title") != null) r.setTitle((String) body.get("title"));
        if (body.get("description") != null) r.setDescription((String) body.get("description"));
        if (body.get("coverImage") != null) r.setCoverImage((String) body.get("coverImage"));
        if (body.get("duration") != null) r.setDuration((String) body.get("duration"));
        if (body.get("city") != null) r.setCity((String) body.get("city"));
        if (body.get("tags") != null) r.setTags((String) body.get("tags"));
    }
}
