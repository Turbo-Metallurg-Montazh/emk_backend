package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTender;
import org.springframework.data.repository.CrudRepository;

public interface FoundTenderRepository extends CrudRepository<FoundTender, Long> {
}
