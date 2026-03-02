package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCode(String code);

    Optional<Role> findByName(String name);

    @Query("""
            SELECT DISTINCT r
            FROM Role r
            LEFT JOIN FETCH r.permissions
            """)
    List<Role> findAllWithPermissions();

    @Query("""
            SELECT DISTINCT r
            FROM Role r
            LEFT JOIN FETCH r.permissions
            WHERE r.code = :code
            """)
    Optional<Role> findByCodeWithPermissions(@Param("code") String code);
}
