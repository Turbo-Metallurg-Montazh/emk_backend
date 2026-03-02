package com.kindred.emkcrm_project_backend.db.entities.favoritesEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kindred.emkcrm_project_backend.db.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
        name = "favorite_markers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_favorite_markers_marker_id", columnNames = {"marker_id"})
        }
)
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteMarker extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    @Column(name = "db_id")
    private Long dbId;

    @JsonProperty("Id")
    @Column(name = "marker_id", nullable = false, length = 255)
    private String markerId;

    @JsonProperty("Name")
    @Column(name = "name", length = 255)
    private String name;

    @JsonIgnore
    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;
}
