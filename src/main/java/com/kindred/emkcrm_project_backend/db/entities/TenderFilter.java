package com.kindred.emkcrm_project_backend.db.entities;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Entity
@Table(name = "tender_filter")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString
public class TenderFilter extends AuditableEntity {
    public TenderFilter(String name, long userId, boolean active, String jsonFilter) {
        this.name = name;
        this.userId = userId;
        this.active = active;
        this.jsonFilter = jsonFilter;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "json_filter", columnDefinition = "TEXT", nullable = false)
    private String jsonFilter;
}
