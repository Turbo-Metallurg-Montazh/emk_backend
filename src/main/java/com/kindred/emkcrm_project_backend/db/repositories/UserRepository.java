package com.kindred.emkcrm_project_backend.db.repositories;

import com.kindred.emkcrm_project_backend.db.entities.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    Optional<User> findOptionalByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "groups", "groups.roles", "groups.roles.permissions"})
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameWithAuthorities(@Param("username") String username);

}
