package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.converter.BirthdayConverter;
import org.example.entity.Audit;
import org.example.listener.AuditTableListener;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        SessionFactory factory = buildConfiguration()
                .configure()
                .buildSessionFactory();
        registerListeners(factory);
        return factory;
    }

    @SuppressWarnings("DataFlowIssue")
    private static void registerListeners(SessionFactory factory) {
        SessionFactoryImpl sessionFactory = factory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry()
                .getService(EventListenerRegistry.class);
        AuditTableListener listener = new AuditTableListener();
        registry.appendListeners(EventType.PRE_DELETE, listener);
        registry.appendListeners(EventType.PRE_INSERT, listener);
    }

    public static Configuration buildConfiguration() {
        return new Configuration()
                .addAnnotatedClass(Audit.class)
                .addAttributeConverter(BirthdayConverter.class)
                .setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
    }
}
