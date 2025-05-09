package org.example.dao;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<ID extends Serializable, E extends BaseEntity<ID>> implements Repository<ID, E> {
    @Getter
    private final EntityManager manager;
    private final Class<E> clazz;

    @Override
    public E save(E entity) {
        manager.persist(entity);
        return entity;
    }

    @Override
    public void delete(ID id) {
        var cb = manager.getCriteriaBuilder();
        var delete = cb.createCriteriaDelete(clazz);
        var root = delete.from(clazz);
        delete.where(cb.equal(root.get("id"), id));
        manager.createQuery(delete).executeUpdate();
    }

    @Override
    public void update(E entity) {
        manager.merge(entity);
    }

    @Override
    public Optional<E> findById(ID id) {
        return Optional.ofNullable(manager.find(clazz, id));
    }

    @Override
    public List<E> findAll() {
        var criteria = manager.getCriteriaBuilder().createQuery(clazz);
        criteria.from(clazz);
        return manager.createQuery(criteria).getResultList();
    }
}
