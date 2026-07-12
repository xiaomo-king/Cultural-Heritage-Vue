package com.jiangyou.repository;
import com.jiangyou.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByCityAndStatus(String city, Integer status);
    List<Route> findByStatus(Integer status);
    List<Route> findAllByOrderByCreatedAtDesc();
}