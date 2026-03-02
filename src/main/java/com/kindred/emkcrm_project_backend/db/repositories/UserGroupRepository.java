package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    Optional<UserGroup> findByCode(String code);
}
