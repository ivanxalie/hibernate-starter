package org.example.entity;

import com.querydsl.core.annotations.QueryExclude;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.listener.AuditDatesListener;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditDatesListener.class)
@QueryExclude
public abstract class AuditableEntity<T extends Serializable> implements BaseEntity<T> {
    private Instant createdAt;
    private String createdBy;

    private Instant updatedAt;
    private String updatedBy;
}
