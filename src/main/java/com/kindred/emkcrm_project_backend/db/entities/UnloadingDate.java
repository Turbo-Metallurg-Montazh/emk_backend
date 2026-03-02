package com.kindred.emkcrm_project_backend.db.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "unloading_date")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString
public class UnloadingDate extends AuditableEntity {
    public UnloadingDate(long filterId, LocalDateTime unloadingDate) {
        this.filterId = filterId;
        this.unloadDate = unloadingDate;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "filter_id", nullable = false)
    private long filterId;

    @Column(name = "unload_date")
    private LocalDateTime unloadDate;
}
