package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteTender;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.TenderSourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteTenderRepository extends JpaRepository<FavoriteTender, Long> {
    Optional<FavoriteTender> findByPurchaseIdAndSourceTypeAndMarkerName(String purchaseId, TenderSourceType sourceType, String markerName);
}
