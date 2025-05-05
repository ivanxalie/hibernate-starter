package org.example.dao;

import jakarta.persistence.Tuple;
import org.example.dto.CompanyDto;
import org.example.dto.PaymentFilter;
import org.example.entity.Payment;
import org.example.entity.User;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

public interface UserDao {
    List<User> findAll(Session session);

    List<User> findAllByFirstName(Session session, String firstName);

    List<User> findLimitedUsersOrderedByBirthday(Session session, int limit);

    List<User> findAllByCompanyName(Session session, String companyName);

    List<Payment> findAllPaymentsByCompanyName(Session session, String companyName);

    default Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return 0.0;
    }

    default Double findAveragePaymentAmountByFirstAndLastNames(Session session, PaymentFilter filter) {
        return 0.0;
    }

    default List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return Collections.emptyList();
    }

    default List<CompanyDto> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyNameDto(Session session) {
        return Collections.emptyList();
    }

    default List<com.querydsl.core.Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyNameTuple(Session session) {
        return Collections.emptyList();
    }

    default List<Object[]> isItPossible(Session session) {
        return Collections.emptyList();
    }

    default List<Tuple> isItPossibleTuple(Session session) {
        return Collections.emptyList();
    }

    default List<com.querydsl.core.Tuple> isItPossibleTupleQueryDsl(Session session) {
        return Collections.emptyList();
    }
}
