package com.project.cuecards.entities;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role extends BaseEntity implements GrantedAuthority {
    @NotBlank
    @Column(unique = true)
    private String name;
    private String description;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role() {
        this.users = new HashSet<>();
    }

    public Role(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
