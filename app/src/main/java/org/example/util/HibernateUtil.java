package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.converter.BirthdayConverter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        return buildConfiguration()
                .configure()
                .buildSessionFactory();
    }

    public static Configuration buildConfiguration() {
        return new Configuration()
                .addAttributeConverter(BirthdayConverter.class)
                .setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
    }
}
