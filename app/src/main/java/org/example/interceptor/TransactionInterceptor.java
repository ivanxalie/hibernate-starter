package org.example.interceptor;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TransactionInterceptor {
    private final SessionFactory factory;

    @RuntimeType
    @SneakyThrows
    public Object intercept(@SuperCall Callable<Object> callable, @Origin Method method) {
        Transaction transaction = null;
        boolean transactionStarted = false;
        if (method.isAnnotationPresent(Transactional.class)) {
            transaction = factory.getCurrentSession().getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
                transactionStarted = true;
            }
        }
        Object result;
        try {
            result = callable.call();
            if (transactionStarted) transaction.commit();
        } catch (Exception e) {
            if (transactionStarted) transaction.rollback();
            throw e;
        }

        return result;
    }
}
