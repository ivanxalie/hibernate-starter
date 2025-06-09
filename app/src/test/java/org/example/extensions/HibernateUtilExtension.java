package org.example.extensions;

import org.example.util.HibernateTestUtil;
import org.example.util.TestDataImporter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.util.UUID;

public class HibernateUtilExtension implements ParameterResolver, BeforeEachCallback, AfterEachCallback,
        BeforeAllCallback, AfterAllCallback {
    private static final Namespace HIBERNATE_UTIL_NAMESPACE = Namespace.create(HibernateUtilExtension.class);
    private final SessionFactory factory = HibernateTestUtil.buildSessionFactory();
    private final UUID session_id = UUID.randomUUID();
    private final UUID transaction_id = UUID.randomUUID();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(Session.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Store store = extensionContext.getStore(HIBERNATE_UTIL_NAMESPACE);
        return store.get(session_id, Session.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Store store = context.getStore(HIBERNATE_UTIL_NAMESPACE);
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        store.put(session_id, session);
        store.put(transaction_id, transaction);

    }

    @Override
    public void afterEach(ExtensionContext context) {
        Store store = context.getStore(HIBERNATE_UTIL_NAMESPACE);
        Session session = store.get(session_id, Session.class);
        Transaction transaction = store.get(transaction_id, Transaction.class);
        transaction.commit();
        session.close();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        TestDataImporter.importData(factory);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        factory.close();
    }
}
