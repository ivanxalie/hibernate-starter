package org.example.dao;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.entity.Payment;
import org.example.entity.QPayment;
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
        return new JPAQuery<User>(session)
                .select(QUser.user)
                .from(QUser.user)
                .fetch();
    }

    @Override
    public List<User> findAllByFirstName(Session session, String firstName) {
        return new JPAQuery<User>(session)
                .select(QUser.user)
                .from(QUser.user)
                .where(QUser.user.personalInfo.firstName.eq(firstName))
                .fetch();
    }

    @Override
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return new JPAQuery<User>(session)
                .select(QUser.user)
                .from(QUser.user)
                .limit(limit)
                .orderBy(QUser.user.personalInfo.birthDate.asc())
                .fetch();
    }

    @Override
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return new JPAQuery<User>(session)
                .select(QUser.user)
                .from(QUser.user)
                .where(QUser.user.company.name.eq(companyName))
                .fetch();
    }

    @Override
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return new JPAQuery<Payment>(session)
                .select(QPayment.payment)
                .from(QPayment.payment)
                .where(QPayment.payment.receiver.company.name.eq(companyName))
                .fetch();
    }

    @Override
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return new JPAQuery<Double>(session)
                .select(QPayment.payment.amount.avg())
                .from(QPayment.payment)
                .where(
                        QPayment.payment.receiver.personalInfo.firstName.eq(firstName),
                        QPayment.payment.receiver.personalInfo.lastName.eq(lastName)
                )
                .groupBy(QPayment.payment.receiver.id)
                .fetchOne();
    }
}
