package com.project.cuecards.entities;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;

    @CreatedDate
    private LocalDateTime createdDateTime;

    @CreatedBy
    @ManyToOne
    private User createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDateTime;

    @LastModifiedBy
    @ManyToOne
    private User lastModifiedBy;

    public Long getId() {
        return id;
    }

    public BaseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public BaseEntity setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public BaseEntity setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public BaseEntity setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public BaseEntity setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
        return this;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public BaseEntity setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }
}
