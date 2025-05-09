package org.example.repository;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.dao.Repository;
import org.example.entity.BaseEntity;
import org.hibernate.jpa.SpecHints;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
    public EntityGraph<E> createAndPutEntityGraphIntoMap(Map<String, Object> properties) {
        EntityGraph<E> graph = manager.createEntityGraph(clazz);
        properties.put(SpecHints.HINT_SPEC_FETCH_GRAPH, graph);
        return graph;
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
    public Optional<E> findById(ID id, Map<String, Object> props) {
        return Optional.ofNullable(manager.find(clazz, id, props));
    }

    @Override
    public List<E> findAll() {
        var criteria = manager.getCriteriaBuilder().createQuery(clazz);
        criteria.from(clazz);
        return manager.createQuery(criteria).getResultList();
    }
}
