package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.User;

public class UserRepository extends BaseRepository<Long, User> {
    public UserRepository(EntityManager manager) {
        super(manager, User.class);
    }
}
