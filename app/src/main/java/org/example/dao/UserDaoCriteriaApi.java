package org.example.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.entity.*;
import org.hibernate.Session;

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
        var firstNamePath = user.get(User_.personalInfo).get(PersonalInfo_.firstName);
        criteria.select(user).where(cb.equal(firstNamePath, firstName));
        return session.createQuery(criteria).list();
    }

    @Override
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        var birthDatePath = user.get(User_.personalInfo).get(PersonalInfo_.birthDate);
        criteria.select(user).fetch(limit).orderBy(cb.asc(birthDatePath));
        return session.createQuery(criteria).list();
    }

    @Override
    public List<User> findAllByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        var companyNamePath = user.get(User_.company).get(Company_.name);
        criteria.select(user).where(cb.equal(companyNamePath, companyName));
        return session.createQuery(criteria).list();
    }

    @Override
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Payment.class);
        var payment = criteria.from(Payment.class);
        var companyNamePath = payment.get(Payment_.receiver).get(User_.company).get(Company_.name);
        criteria.select(payment).where(cb.equal(companyNamePath, companyName));
        return session.createQuery(criteria).list();
    }

    @Override
    public Double findAveragePaymentAmountByFirstAndLastName(Session session, String firstName, String lastName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Double.class);
        var payment = criteria.from(Payment.class);
        var firstNamePath = payment.get(Payment_.receiver).get(User_.personalInfo).get(PersonalInfo_.firstName);
        var lastNamePath = payment.get(Payment_.receiver).get(User_.personalInfo).get(PersonalInfo_.lastName);
        var predicate = cb.and(
                cb.equal(firstNamePath, firstName),
                cb.equal(lastNamePath, lastName)
        );
        criteria.select(cb.avg(payment.get(Payment_.amount))).where(predicate);

        return session.createQuery(criteria).uniqueResult();
    }

    @Override
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Object[].class);
        var payment = criteria.from(Payment.class);
        var companyNamePath = payment.get(Payment_.receiver).get(User_.company).get(Company_.name);

        criteria
                .multiselect(companyNamePath, cb.avg(payment.get(Payment_.amount)))
                .orderBy(cb.asc(companyNamePath))
                .groupBy(companyNamePath);

        return session.createQuery(criteria).list();
    }

    @Override
    public List<Object[]> isItPossible(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Object[].class);

        var user = criteria.from(User.class);
        var payments = user.join(User_.payments);

        var subquery = criteria.subquery(Double.class);
        var subqueryPayment = subquery.from(Payment.class);
        subquery.select(cb.avg(subqueryPayment.get(Payment_.amount)));

        var avg = cb.avg(payments.get(Payment_.amount));

        var having = cb.greaterThan(avg, subquery);

        var firstNamePath = user.get(User_.personalInfo).get(PersonalInfo_.firstName);

        criteria
                .multiselect(user, avg)
                .groupBy(user)
                .having(having)
                .orderBy(cb.asc(firstNamePath));

        return session.createQuery(criteria).list();
    }
}
