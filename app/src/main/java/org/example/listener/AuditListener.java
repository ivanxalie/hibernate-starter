package org.example.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.example.entity.AuditableEntity;

import java.time.Instant;

public class AuditListener {
    @PrePersist
    public void prePersist(AuditableEntity<?> entity) {
        entity.setCreatedAt(Instant.now());
        entity.setCreatedBy("ME!!!");
    }

    @PreUpdate
    public void preUpdate(AuditableEntity<?> entity) {
        entity.setUpdatedAt(Instant.now());
        entity.setUpdatedBy("ME!!!!!!!!!!!!!!!!!");
    }
}
