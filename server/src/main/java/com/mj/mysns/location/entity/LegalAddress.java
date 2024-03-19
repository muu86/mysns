package com.mj.mysns.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

@Entity
public class LegalAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String sido;

    private String gungu;

    private String eupmyundong;

    private String li;

    // 순위?? 가 뭐냐
    private String sunwi;

    @Column(nullable = false, columnDefinition = "geometry(Geometry, 4326)")
//    @Column(nullable = false, columnDefinition = "GEOMETRY SRID 4326")
    private Geometry location;

//    private Polygon polygon;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime deletedAt;

    private String prevCode;
}
