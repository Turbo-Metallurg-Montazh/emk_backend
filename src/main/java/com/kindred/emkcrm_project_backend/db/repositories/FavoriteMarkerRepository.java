package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteMarker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteMarkerRepository extends JpaRepository<FavoriteMarker, Long> {
    Optional<FavoriteMarker> findByMarkerId(String markerId);
}
