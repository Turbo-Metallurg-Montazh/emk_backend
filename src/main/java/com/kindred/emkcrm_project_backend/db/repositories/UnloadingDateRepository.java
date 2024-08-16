package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.UnloadingDate;
import org.springframework.data.repository.CrudRepository;

public interface UnloadingDateRepository extends CrudRepository<UnloadingDate, Long> {
    UnloadingDate findTopByFilterIdOrderByUnloadDateDesc(long filterId);
}
