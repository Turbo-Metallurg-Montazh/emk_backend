package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.UserPermissionOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPermissionOverrideRepository extends JpaRepository<UserPermissionOverride, Long> {

    List<UserPermissionOverride> findByUser_Id(Long userId);
}
