package org.example.dao;

import org.example.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Repository<ID extends Serializable, E extends BaseEntity<ID>> {
    E save(E entity);

    default void delete(E entity) {
        if (entity.getId() != null)
            delete(entity.getId());
        else throw new RuntimeException("Id cannot be null!");
    }

    void delete(ID id);

    void update(E entity);

    Optional<E> findById(ID id);

    List<E> findAll();
}

