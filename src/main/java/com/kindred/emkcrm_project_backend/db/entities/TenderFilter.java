package com.kindred.emkcrm_project_backend.db.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tender_filter")
@Data
@NoArgsConstructor
public class TenderFilter {
    public TenderFilter(String name, long userId, boolean active, String jsonFilter) {
        this.name = name;
        this.userId = userId;
        this.active = active;
        this.jsonFilter = jsonFilter;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "json_filter",  columnDefinition = "TEXT")
    private String jsonFilter;


}
