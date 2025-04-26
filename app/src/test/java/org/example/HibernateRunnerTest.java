package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.example.entity.Birthday;
import org.example.entity.PersonalInfo;
import org.example.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkReflectionApi() {
        User user = User.builder()
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