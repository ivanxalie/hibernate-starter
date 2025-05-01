package org.example.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.entity.Payment;
import org.example.entity.User;
import org.hibernate.Session;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {
    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    public List<User> findAll(Session session) {
        return session.createQuery("from User", User.class).list();
    }

    public List<User> findAllByFirstName(Session session, String firstName) {
        return session
                .createQuery(
                        "select u from User u where u.personalInfo.firstName = :firstName",
                        User.class)
                .setParameter("firstName", firstName)
                .list();
    }

    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return session
                .createQuery("select u from User u order by u.personalInfo.birthDate limit :limit",
                        User.class)
                .setParameter("limit", limit)
                .list();
    }

    public List<User> findAllByCompanyName(Session session, String companyName) {
        return session
                .createQuery("select u from User u where u.company.name = :companyName", User.class)
                .setParameter("companyName", companyName)
                .list();
    }

    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return session
                .createQuery("""
                                select p\s
                                from Payment p\s
                                where p.receiver.company.name = :companyName\s
                                order by p.receiver.personalInfo.firstName, p.amount
                                """,
                        Payment.class)
                .setParameter("companyName", companyName)
                .list();
    }

    public Double findAveragePaymentAmountByFirstAndLastName(Session session, String firstName, String lastName) {
        return session
                .createQuery(
                        """
                                select avg(p.amount)\s
                                from Payment p\s
                                where p.receiver.personalInfo.firstName = :firstName\s
                                and p.receiver.personalInfo.lastName = :lastName\s
                                group by p.receiver.id
                               """,
                        Double.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
    }

    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return session
                .createQuery("""
                        select p.receiver.company.name, avg(p.amount)\s
                                from Payment p\s
                                group by p.receiver.company.name\s
                                order by 1
                        """, Object[].class)
                .list();
    }

    public List<Object[]> isItPossible(Session session) {
        return session
                .createQuery("""
                        select u, avg(p.amount)
                        from User u
                        join u.payments p
                        group by u
                        having avg(p.amount) > (select avg(p.amount) from Payment p)
                        order by u.personalInfo.firstName
                        """, Object[].class)
                .list();
    }
}
