package org.example.dao;

import jakarta.persistence.EntityGraph;
import org.example.entity.BaseEntity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Repository<ID extends Serializable, E extends BaseEntity<ID>> {
    E save(E entity);

    default void delete(E entity) {
        if (entity.getId() != null)
            delete(entity.getId());
        else throw new RuntimeException("Id cannot be null!");
    }

    default EntityGraph<E> createAndPutEntityGraphIntoMap(Map<String, Object> properties) {
        return null;
    }

    void delete(ID id);

    void update(E entity);

    default Optional<E> findById(ID id) {
        return findById(id, Collections.emptyMap());
    }

    Optional<E> findById(ID id, Map<String, Object> props);


    List<E> findAll();
}

