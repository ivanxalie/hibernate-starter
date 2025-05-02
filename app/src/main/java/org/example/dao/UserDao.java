package org.example.dao;

import org.example.entity.Payment;
import org.example.entity.User;
import org.hibernate.Session;

import java.util.List;

public interface UserDao {
    List<User> findAll(Session session);

    List<User> findAllByFirstName(Session session, String firstName);

    List<User> findLimitedUsersOrderedByBirthday(Session session, int limit);

    List<User> findAllByCompanyName(Session session, String companyName);

    List<Payment> findAllPaymentsByCompanyName(Session session, String companyName);

    Double findAveragePaymentAmountByFirstAndLastName(Session session, String firstName, String lastName);

    List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session);

    List<Object[]> isItPossible(Session session);
}
