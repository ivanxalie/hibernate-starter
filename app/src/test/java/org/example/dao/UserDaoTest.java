package org.example.dao;

import org.example.dto.PaymentFilter;
import org.example.entity.Payment;
import org.example.entity.User;
import org.example.extensions.HibernateUtilExtension;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(HibernateUtilExtension.class)
public class UserDaoTest {
    private final UserDao userDao = UserDaoQueryDsl.getInstance();

    @Test
    void findAll(Session session) {
        List<User> users = userDao.findAll(session);
        assertThat(users).isNotNull().hasSize(5);

        List<String> fullNames = users.stream().map(User::fullName).toList();
        assertThat(fullNames).containsExactlyInAnyOrder(
                "Bill Gates", "Steve Jobs", "Sergey Brin", "Tim Cook", "Diane Greene");
    }

    @Test
    void findAllByFirstName(Session session) {
        List<User> users = userDao.findAllByFirstName(session, "Bill");

        assertThat(users).isNotNull().hasSize(1);
        assertThat(users.getFirst().fullName()).isEqualTo("Bill Gates");
    }

    @Test
    void findLimitedUsersOrderedByBirthday(Session session) {
        int limit = 3;
        List<User> users = userDao.findLimitedUsersOrderedByBirthday(session, limit);
        assertThat(users).isNotNull().hasSize(limit);

        List<String> fullNames = users.stream().map(User::fullName).toList();
        assertThat(fullNames).isNotNull().contains("Diane Greene", "Steve Jobs", "Bill Gates");
    }

    @Test
    void findAllByCompanyName(Session session) {
        List<User> users = userDao.findAllByCompanyName(session, "Google");
        assertThat(users).isNotNull().hasSize(2);

        List<String> fullNames = users.stream().map(User::fullName).toList();
        assertThat(fullNames).isNotNull().contains("Sergey Brin", "Diane Greene");
    }

    @Test
    void findAllPaymentsByCompanyName(Session session) {
        List<Payment> applePayments = userDao.findAllPaymentsByCompanyName(session, "Apple");
        assertThat(applePayments).isNotNull().hasSize(5);

        List<Integer> amounts = applePayments.stream().map(Payment::getAmount).toList();
        assertThat(amounts).contains(250, 500, 600, 300, 400);
    }

    @Test
    void findAveragePaymentAmountByFirstAndLastNames(Session session) {
        Double averagePaymentAmount = userDao.findAveragePaymentAmountByFirstAndLastNames(session,
                PaymentFilter.builder()
                        .firstName("Bill")
                        .lastName("Gates")
                        .build());
        assertThat(averagePaymentAmount).isNotNull().isEqualTo(300.0);
    }

    @Test
    void findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        List<com.querydsl.core.Tuple> result = userDao
                .findCompanyNamesWithAvgUserPaymentsOrderedByCompanyNameTuple(session);
        assertThat(result).isNotNull().hasSize(3);

        List<String> orgNames = result.stream().map(it -> it.get(0, String.class)).toList();
        assertThat(orgNames).contains("Apple", "Google", "Microsoft");

        List<Double> orgAvgPayments = result.stream().map(it -> it.get(1, Double.class)).toList();
        assertThat(orgAvgPayments).contains(410.0, 400.0, 300.0);
    }

    @Test
    void isItPossible(Session session) {
        List<com.querydsl.core.Tuple> result = userDao.isItPossibleTupleQueryDsl(session);
        assertThat(result).isNotNull().hasSize(2);

        List<String> names = result.stream().map(it -> it.get(0, User.class).fullName()).toList();
        assertThat(names).contains("Sergey Brin", "Steve Jobs");

        List<Double> averagePayments = result.stream().map(it -> it.get(1, Double.class)).toList();
        assertThat(averagePayments).contains(500.0, 450.0);
    }
}
