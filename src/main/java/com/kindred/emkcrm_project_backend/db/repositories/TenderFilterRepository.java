package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.TenderFilter;
import org.springframework.data.repository.CrudRepository;

public interface TenderFilterRepository extends CrudRepository<TenderFilter, Long> {
    Iterable<TenderFilter> findAllByActiveIs(boolean active);
    Iterable<TenderFilter> findAllByUserId(long userId);
}
