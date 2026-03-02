package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.purchaseResultsEntity.PurchaseResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseResultRepository extends JpaRepository<PurchaseResult, Long> {
    Optional<PurchaseResult> findByPurchaseId(String purchaseId);
}
