package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundTenderRepository extends JpaRepository<FoundTender, Long> {
}
