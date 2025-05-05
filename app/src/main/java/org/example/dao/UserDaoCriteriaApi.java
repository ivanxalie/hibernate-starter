package org.example.dao;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.dto.CompanyDto;
import org.example.entity.*;
import org.hibernate.Session;

import java.util.ArrayList;
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
        var company = criteria.from(Company.class);
        var users = company.join(Company_.users);

        criteria.select(users).where(
                cb.equal(company.get(Company_.name), companyName)
        );

        return session.createQuery(criteria).list();
    }

    @Override
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Payment.class);
        var payment = criteria.from(Payment.class);
        var user = payment.join(Payment_.receiver);
        var company = user.join(User_.company);

        criteria.select(payment).where(
                cb.equal(company.get(Company_.name), companyName)
        ).orderBy(
                cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstName)),
                cb.asc(payment.get(Payment_.amount))
        );

        return session.createQuery(criteria).list();
    }

    @Override
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Double.class);
        var payment = criteria.from(Payment.class);
        var user = payment.join(Payment_.receiver);

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null)
            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstName), firstName));
        if (lastName != null)
            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.lastName), lastName));

        criteria
                .select(cb.avg(payment.get(Payment_.amount)))
                .where(predicates.toArray(Predicate[]::new));

        return session.createQuery(criteria).uniqueResult();
    }

    @Override
    public List<CompanyDto> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyNameDto(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(CompanyDto.class);
        var company = criteria.from(Company.class);
        var user = company.join(Company_.users, JoinType.INNER);
        var payment = user.join(User_.payments);

        criteria
                .select(
                        cb.construct(CompanyDto.class,
                                company.get(Company_.name), cb.avg(payment.get(Payment_.amount))
                        ))
                .groupBy(company.get(Company_.name))
                .orderBy(cb.asc(company.get(Company_.name)));

        return session.createQuery(criteria).list();
    }

    @Override
    public List<Tuple> isItPossibleTuple(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createTupleQuery();

// FROM User
        var user = criteria.from(User.class);

// JOIN payments
        var payments = user.join("payments");

// Подзапрос со средним значением по всем
        var subquery = criteria.subquery(Double.class);
        var subRoot = subquery.from(Payment.class);
        subquery.select(cb.avg(subRoot.get("amount")));

// AVG по текущему пользователю
        var avgAmount = cb.avg(payments.get("amount"));

// HAVING avg(user) > avg(all)
        Predicate having = cb.greaterThan(avgAmount, subquery);

// Сортировка по embedded полю
        var firstNamePath = user.get("personalInfo").get("firstName");

// ⚠️ Добавь alias-ы, иначе Tuple — это просто Object[] с шапкой
        criteria
                .multiselect(
                        user.alias("user"),
                        avgAmount.alias("avgAmount")
                )
                .groupBy(user)
                .having(having)
                .orderBy(cb.asc(firstNamePath));

// Выполнение
        return session.createQuery(criteria).getResultList();
    }
}
