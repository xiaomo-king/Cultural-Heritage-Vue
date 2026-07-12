package com.jiangyou.repository;
import com.jiangyou.model.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    List<RoutePoint> findByRouteIdOrderByPointOrderAsc(Long routeId);
}