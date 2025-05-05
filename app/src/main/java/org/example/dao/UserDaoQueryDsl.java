package org.example.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.dto.PaymentFilter;
import org.example.entity.Payment;
import org.example.entity.User;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static org.example.entity.QCompany.company;
import static org.example.entity.QPayment.payment;
import static org.example.entity.QUser.user;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDaoQueryDsl implements UserDao {
    private static final UserDaoQueryDsl INSTANCE = new UserDaoQueryDsl();

    public static UserDaoQueryDsl getInstance() {
        return INSTANCE;
    }

    @Override
    public List<User> findAll(Session session) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .fetch();
    }

    @Override
    public List<User> findAllByFirstName(Session session, String firstName) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.personalInfo.firstName.eq(firstName))
                .fetch();
    }

    @Override
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .limit(limit)
                .orderBy(user.personalInfo.birthDate.asc())
                .fetch();
    }

    @Override
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.company.name.eq(companyName))
                .fetch();
    }

    @Override
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return new JPAQuery<Payment>(session)
                .select(payment)
                .from(payment)
                .where(payment.receiver.company.name.eq(companyName))
                .orderBy(payment.receiver.personalInfo.firstName.asc(), payment.amount.asc())
                .fetch();
    }

    @Override
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, PaymentFilter filter) {
        return new JPAQuery<Double>(session)
                .select(payment.amount.avg())
                .from(payment)
                .where(QPredicate.builder()
                        .add(filter.getFirstName(), payment.receiver.personalInfo.firstName::eq)
                        .add(filter.getLastName(), payment.receiver.personalInfo.lastName::eq)
                        .buildAnd())
                .groupBy(payment.receiver.id)
                .fetchOne();
    }

    @Override
    public List<Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyNameTuple(Session session) {
        return new JPAQuery<Tuple>(session)
                .select(company.name, payment.amount.avg())
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .groupBy(company.name)
                .orderBy(company.name.asc())
                .fetch();
    }

    @Override
    public List<Tuple> isItPossibleTupleQueryDsl(Session session) {
        return new JPAQuery<Tuple>(session)
                .select(user, payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .groupBy(user)
                .having(payment.amount.avg().gt(
                        new JPAQuery<Double>(session)
                                .select(payment.amount.avg())
                                .from(payment)
                ))
                .orderBy(user.personalInfo.firstName.asc())
                .fetch();
    }
}
