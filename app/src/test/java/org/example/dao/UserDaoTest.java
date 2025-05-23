package org.example.dao;

import lombok.Cleanup;
import org.example.dto.PaymentFilter;
import org.example.entity.Payment;
import org.example.entity.User;
import org.example.util.HibernateTestUtil;
import org.example.util.TestDataImporter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoTest {
    private final SessionFactory factory = HibernateTestUtil.buildSessionFactory();
    private final UserDao userDao = UserDaoQueryDsl.getInstance();

    @BeforeAll
    public void initDb() {
        TestDataImporter.importData(factory);
    }

    @AfterAll
    public void finish() {
        factory.close();
    }

    @Test
    void findAll() {
        execute(session -> {
            List<User> users = userDao.findAll(session);
            assertThat(users).isNotNull().hasSize(5);

            List<String> fullNames = users.stream().map(User::fullName).toList();
            assertThat(fullNames).containsExactlyInAnyOrder(
                    "Bill Gates", "Steve Jobs", "Sergey Brin", "Tim Cook", "Diane Greene");
        });
    }

    @Test
    void findAllByFirstName() {
        execute(session -> {
            List<User> users = userDao.findAllByFirstName(session, "Bill");

            assertThat(users).isNotNull().hasSize(1);
            assertThat(users.getFirst().fullName()).isEqualTo("Bill Gates");
        });
    }

    @Test
    void findLimitedUsersOrderedByBirthday() {
        execute(session -> {
            int limit = 3;
            List<User> users = userDao.findLimitedUsersOrderedByBirthday(session, limit);
            assertThat(users).isNotNull().hasSize(limit);

            List<String> fullNames = users.stream().map(User::fullName).toList();
            assertThat(fullNames).isNotNull().contains("Diane Greene", "Steve Jobs", "Bill Gates");
        });
    }

    @Test
    void findAllByCompanyName() {
        execute(session -> {
            List<User> users = userDao.findAllByCompanyName(session, "Google");
            assertThat(users).isNotNull().hasSize(2);

            List<String> fullNames = users.stream().map(User::fullName).toList();
            assertThat(fullNames).isNotNull().contains("Sergey Brin", "Diane Greene");
        });
    }

    @Test
    void findAllPaymentsByCompanyName() {
        execute(session -> {
            List<Payment> applePayments = userDao.findAllPaymentsByCompanyName(session, "Apple");
            assertThat(applePayments).isNotNull().hasSize(5);

            List<Integer> amounts = applePayments.stream().map(Payment::getAmount).toList();
            assertThat(amounts).contains(250, 500, 600, 300, 400);
        });
    }

    @Test
    void findAveragePaymentAmountByFirstAndLastNames() {
        execute(session -> {
            Double averagePaymentAmount = userDao.findAveragePaymentAmountByFirstAndLastNames(session,
                    PaymentFilter.builder()
                            .firstName("Bill")
                            .lastName("Gates")
                            .build());
            assertThat(averagePaymentAmount).isNotNull().isEqualTo(300.0);
        });
    }

    @Test
    void findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName() {
        execute(session -> {
            List<com.querydsl.core.Tuple> result = userDao
                    .findCompanyNamesWithAvgUserPaymentsOrderedByCompanyNameTuple(session);
            assertThat(result).isNotNull().hasSize(3);

            List<String> orgNames = result.stream().map(it -> it.get(0, String.class)).toList();
            assertThat(orgNames).contains("Apple", "Google", "Microsoft");

            List<Double> orgAvgPayments = result.stream().map(it -> it.get(1, Double.class)).toList();
            assertThat(orgAvgPayments).contains(410.0, 400.0, 300.0);
        });
    }

    @Test
    void isItPossible() {
        execute(session -> {
            List<com.querydsl.core.Tuple> result = userDao.isItPossibleTupleQueryDsl(session);
            assertThat(result).isNotNull().hasSize(2);

            List<String> names = result.stream().map(it -> it.get(0, User.class).fullName()).toList();
            assertThat(names).contains("Sergey Brin", "Steve Jobs");

            List<Double> averagePayments = result.stream().map(it -> it.get(1, Double.class)).toList();
            assertThat(averagePayments).contains(500.0, 450.0);
        });
    }


    private void execute(Consumer<Session> businessLogic) {
        @Cleanup var session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        businessLogic.accept(session);
        transaction.commit();
    }
}
