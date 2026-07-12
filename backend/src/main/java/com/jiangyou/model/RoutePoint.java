package com.jiangyou.model;
import jakarta.persistence.*;
@Entity @Table(name = "route_point")
public class RoutePoint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "route_id", nullable = false) private Long routeId;
    @Column(name = "heritage_id", nullable = false) private Long heritageId;
    @Column(name = "point_order") private Integer pointOrder = 0;
    @Column(name = "stay_time", length = 32) private String stayTime = "";
    @Column(length = 256) private String note = "";
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getRouteId() { return routeId; } public void setRouteId(Long routeId) { this.routeId = routeId; }
    public Long getHeritageId() { return heritageId; } public void setHeritageId(Long heritageId) { this.heritageId = heritageId; }
    public Integer getPointOrder() { return pointOrder; } public void setPointOrder(Integer pointOrder) { this.pointOrder = pointOrder; }
    public String getStayTime() { return stayTime; } public void setStayTime(String stayTime) { this.stayTime = stayTime; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
}