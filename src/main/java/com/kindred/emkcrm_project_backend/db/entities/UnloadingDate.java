package com.kindred.emkcrm_project_backend.db.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "unloading_date")
@Data
@NoArgsConstructor
public class UnloadingDate {
    public UnloadingDate(long filterId, LocalDateTime unloadingDate) {
        this.filterId = filterId;
        this.unloadDate = unloadingDate;
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "filter_id")
    private long filterId;

    @Column(name = "unload_date")
    private LocalDateTime unloadDate;


}
