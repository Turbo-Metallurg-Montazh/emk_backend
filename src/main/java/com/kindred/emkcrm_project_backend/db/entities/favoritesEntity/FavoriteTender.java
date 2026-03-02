package com.kindred.emkcrm_project_backend.db.entities.favoritesEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kindred.emkcrm_project_backend.db.entities.AuditableEntity;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.TenderSourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
        name = "favorite_tenders",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_favorite_tenders_purchase_source_marker", columnNames = {"purchase_id", "source_type", "marker_name"})
        }
)
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteTender extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    @Column(name = "db_id")
    private Long dbId;

    @JsonProperty("Id")
    @Column(name = "purchase_id", nullable = false, length = 255)
    private String purchaseId;

    @JsonProperty("MarkerName")
    @Column(name = "marker_name", nullable = false, length = 255)
    private String markerName = "";

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 16)
    private TenderSourceType sourceType = TenderSourceType.PROD;

    @JsonIgnore
    @Column(name = "page_number")
    private Integer pageNumber;

    @JsonIgnore
    @Column(name = "total_count")
    private Long totalCount;

    @JsonIgnore
    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;
}
