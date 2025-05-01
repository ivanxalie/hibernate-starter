package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Cleanup;
import org.example.entity.*;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jpa.AvailableHints;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.example.util.HibernateTestUtil.buildSessionFactory;

class HibernateRunnerTest {

    @Test
    void checkHql() {
        execute(session ->
                session
                        .createNamedQuery(
                                "findUserByName",
                                User.class
                        )
                        .setParameter("firstName", "ivan")
                        .setParameter("companyName", "Google")
                        .setHint(AvailableHints.HINT_BATCH_FETCH_SIZE, "50")
                        .list().forEach(System.out::println));
    }

    private void execute(Consumer<Session> businessLogic) {
        try (var factory = buildSessionFactory();
             var session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            businessLogic.accept(session);
            transaction.commit();
        }
    }

    @Test
    void checkTestContainers() {
        execute(session -> {
            Company google = Company.builder()
                    .name("Google")
                    .build();

            session.persist(google);

            Programmer programmer = Programmer.builder()
                    .username("ivan@gmail.com")
                    .language(Language.JAVA)
                    .company(google)
                    .build();
            session.persist(programmer);

            Manager manager = Manager.builder()
                    .username("sveta@gmail.com")
                    .projectName("Starter")
                    .company(google)
                    .build();
            session.persist(manager);
            session.flush();

            session.clear();

            var savedProgrammer = session.find(Programmer.class, 1L);
            var savedManager = session.find(User.class, 2L);

            System.out.println(savedProgrammer);
            System.out.println(savedManager);
        });
    }

    @Test
    void sortByUsername() {
        execute(session -> {
            Company company = session.find(Company.class, 18);

            company.getLocales().forEach((key, value) -> System.out.printf("%s:%s%n", key, value));
        });
    }

    @Test
    void localeInfo() {
        execute(session -> {
            Company company = session.find(Company.class, 16);
//            company.getLocales().add(LocaleInfo.of("ru", "Описание на русском"));
//            company.getLocales().add(LocaleInfo.of("en", "English description"));

            company.getLocales().entrySet().forEach(System.out::println);
        });
    }

    @Test
    void checkManyToMany() {
        execute(session -> {
            User user = session.find(User.class, 124L);
            Chat chat = session.find(Chat.class, 1L);

            UserChat userChat = UserChat.builder()
                    .createdAt(Instant.now())
                    .createdBy(user.getUsername())
                    .build();

            userChat.setChat(chat);
            userChat.setUser(user);

            session.persist(userChat);
        });
    }

    @Test
    void checkOneToOne() {
        execute(session -> {
            //            var user = User.builder()
//                    .username("test3@gmail.com")
//                    .build();
//
//            var profile = Profile.builder()
//                    .language("ru")
//                    .street("Example 123")
//                    .build();
//
//            profile.setUser(user);
//
//            session.persist(user);

            User user = session.find(User.class, 124L);
            System.out.println(user);
        });
    }

    @Test
    void checkOrphanRemove() {
        execute(session -> {
            var company = session.getReference(Company.class, 16);
            company.getUsers().entrySet().removeIf(user -> user.getValue().getId() == 30L);
        });
    }

    @Test
    void checkLazyInitialisation() {
        AtomicReference<Company> company = new AtomicReference<>();
        execute(session -> {
            company.set(session.getReference(Company.class, 15));
        });
        System.out.println(company.get().getUsers());
    }

    @Test
    void deleteCompany() {
        @Cleanup var factory = buildSessionFactory();
        @Cleanup var session = factory.openSession();

        Transaction transaction = session.beginTransaction();

        Company company = session.get(Company.class, 14);
        session.remove(company);

        transaction.commit();
    }

    @Test
    void addUserToNewCompany() {
        @Cleanup var factory = buildSessionFactory();
        @Cleanup var session = factory.openSession();

        Transaction transaction = session.beginTransaction();

        var company = Company.builder()
                .name("Facebook")
                .build();

        var user = Manager.builder()
                .username("albina@gmail.com")
                .build();

        company.addUser(user);

        session.persist(company);

        transaction.commit();
    }

    @Test
    void oneToMany() {
        @Cleanup var factory = buildSessionFactory();
        @Cleanup var session = factory.openSession();

        Transaction transaction = session.beginTransaction();

        var company = session.get(Company.class, 5);
        System.out.println(company.getUsers());

        transaction.commit();
    }

    @Test
    void checkReflectionApi() {
        User user = Manager.builder()
                .username("Alex")
                .personalInfo(PersonalInfo.builder()
                        .firstName("Alex")
                        .lastName("Alex")
                        .birthDate(new Birthday(LocalDate.of(1992, Month.APRIL, 12)))
                        .build())
                .build();

        String sql = """
                    insert
                    into
                        %s
                        (%s)
                    values
                        (%s)
                """;

        Class<? extends User> userClass = user.getClass();
        String tableName = Optional
                .ofNullable(userClass.getAnnotation(Table.class))
                .map(table -> (StringUtils.isBlank(table.schema()) ? "public" : table.schema()) + "." + table.name())
                .orElse(userClass.getName());

        Field[] fields = Arrays
                .stream(userClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toArray(Field[]::new);
        String columnNames = Arrays.stream(fields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(","));

        String values = Arrays.stream(fields)
                .map(field -> "?")
                .collect(Collectors.joining(","));

        System.out.printf((sql) + "%n", tableName, columnNames, values);
    }
}