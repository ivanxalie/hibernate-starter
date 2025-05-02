package org.example.dao;

import jakarta.persistence.criteria.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.entity.Payment;
import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.query.criteria.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDaoCriteriaApi implements UserDao {
    private static final UserDaoCriteriaApi INSTANCE = new UserDaoCriteriaApi();

    public static UserDaoCriteriaApi getInstance() {
        return INSTANCE;
    }

    @Override
    public List<User> findAll(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        criteria.select(user);
        return session.createQuery(criteria).list();
    }

    @Override
    public List<User> findAllByFirstName(Session session, String firstName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        Path<String> firstNamePath = user.get("personalInfo").get("firstName");
        criteria.select(user).where(cb.equal(firstNamePath, firstName));
        return session.createQuery(criteria).list();
    }

    @Override
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        Path<String> birthDatePath = user.get("personalInfo").get("birthDate");
        criteria.select(user).fetch(limit).orderBy(cb.asc(birthDatePath));
        return session.createQuery(criteria).list();
    }

    @Override
    public List<User> findAllByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        Path<String> companyNamePath = user.get("company").get("name");
        criteria.select(user).where(cb.equal(companyNamePath, companyName));
        return session.createQuery(criteria).list();
    }

    @Override
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Payment.class);
        var payment = criteria.from(Payment.class);
        Path<String> companyNamePath = payment.get("receiver").get("company").get("name");
        criteria.select(payment).where(cb.equal(companyNamePath, companyName));
        return session.createQuery(criteria).list();
    }

    @Override
    public Double findAveragePaymentAmountByFirstAndLastName(Session session, String firstName, String lastName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Double.class);
        var payment = criteria.from(Payment.class);
        var firstNamePath = payment.get("receiver").get("personalInfo").get("firstName");
        var lastNamePath = payment.get("receiver").get("personalInfo").get("lastName");
        JpaPredicate predicate = cb.and(
                cb.equal(firstNamePath, firstName),
                cb.equal(lastNamePath, lastName)
        );
        criteria.select(cb.avg(payment.get("amount"))).where(predicate);

        return session.createQuery(criteria).uniqueResult();
    }

    @Override
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Object[].class);
        var payment = criteria.from(Payment.class);
        Path<String> companyNamePath = payment.get("receiver").get("company").get("name");

        criteria
                .multiselect(companyNamePath, cb.avg(payment.get("amount")))
                .orderBy(cb.asc(companyNamePath))
                .groupBy(companyNamePath);

        return session.createQuery(criteria).list();
    }

    @Override
    public List<Object[]> isItPossible(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Object[].class);

        JpaRoot<User> user = criteria.from(User.class);
        JpaJoin<User, Payment> payments = user.join("payments");

        JpaSubQuery<Double> subquery = criteria.subquery(Double.class);
        JpaRoot<Payment> subqueryPayment = subquery.from(Payment.class);
        subquery.select(cb.avg(subqueryPayment.get("amount")));

        JpaExpression<Double> avg = cb.avg(payments.get("amount"));

        JpaPredicate having = cb.greaterThan(avg, subquery);

        Path<String> firstNamePath = user.get("personalInfo").get("firstName");

        criteria
                .multiselect(user, avg)
                .groupBy(user)
                .having(having)
                .orderBy(cb.asc(firstNamePath));

        return session.createQuery(criteria).list();
    }
}
