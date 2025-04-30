package org.example.util;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.testcontainers.containers.PostgreSQLContainer;

@UtilityClass
public class HibernateTestUtil {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    static {
        postgres.start();
    }

    public static SessionFactory buildSessionFactory() {
        return HibernateUtil.buildConfiguration()
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .configure()
                .buildSessionFactory();
    }
}
