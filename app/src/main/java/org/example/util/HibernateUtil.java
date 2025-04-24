package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.converter.BirthdayConverter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        return new Configuration()
                .configure()
                .addAttributeConverter(BirthdayConverter.class)
                .setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy())
                .buildSessionFactory();
    }
}
