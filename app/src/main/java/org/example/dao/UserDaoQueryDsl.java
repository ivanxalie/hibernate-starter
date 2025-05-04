package org.example.dao;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.entity.Payment;
import org.example.entity.QUser;
import org.example.entity.User;
import org.hibernate.Session;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDaoQueryDsl implements UserDao {
    private static final UserDaoQueryDsl INSTANCE = new UserDaoQueryDsl();

    public static UserDaoQueryDsl getInstance() {
        return INSTANCE;
    }

    @Override
    public List<User> findAll(Session session) {
        return new JPAQuery<User>(session).select(QUser.user).fetch();
    }

    @Override
    public List<User> findAllByFirstName(Session session, String firstName) {
        return List.of();
    }

    @Override
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return List.of();
    }

    @Override
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return List.of();
    }

    @Override
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return List.of();
    }

    @Override
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return 0.0;
    }
}
