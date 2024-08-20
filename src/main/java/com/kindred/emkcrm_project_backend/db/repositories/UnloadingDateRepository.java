package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.UnloadingDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnloadingDateRepository extends JpaRepository<UnloadingDate, Long> {
    UnloadingDate findTopByFilterIdOrderByUnloadDateDesc(long filterId);
}
