package com.kindred.emkcrm_project_backend.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class Role {
    public Role(String role) {
        this.name = role;
    }

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> users;
    @Override
    public String toString() {
        return String.format("Role{id=%d, name='%s'}", id, name);
    }
}
