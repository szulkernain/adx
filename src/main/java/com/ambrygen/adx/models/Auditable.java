package com.ambrygen.adx.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class Auditable<U> {
    private static final String SYSTEM_USER = "system";


    protected String createdBy;
    protected ZonedDateTime createdDate;
    protected String lastModifiedBy;
    protected ZonedDateTime lastModifiedDate;

    @Id
    protected String id;

    @JsonIgnore
    @Version
    private Long version;

    public Auditable() {
        this.id = UUID.randomUUID().toString();
    }


    @PreUpdate
    @PrePersist
    private void beforeUpdate() {
        updateTimestamps();
        updateUsers();
    }

    private void updateUsers() {
        if (createdBy == null) {
            this.createdBy = SYSTEM_USER;
        }
        if (lastModifiedBy == null) {
            this.lastModifiedBy = SYSTEM_USER;
        }
    }

    private void updateTimestamps() {
        lastModifiedDate = ZonedDateTime.now();
        if (createdDate == null) {
            createdDate = lastModifiedDate;
        }
    }

    public String getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
