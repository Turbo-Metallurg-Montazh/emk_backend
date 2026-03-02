package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.TenderSourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenderRepository extends JpaRepository<Tender, Long> {
    Optional<Tender> findByIdAndSourceType(String id, TenderSourceType sourceType);
}
