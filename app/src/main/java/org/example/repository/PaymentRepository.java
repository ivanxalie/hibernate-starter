package org.example.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.example.entity.Payment;

import java.util.List;

import static org.example.entity.QPayment.payment;

public class PaymentRepository extends BaseRepository<Long, Payment> {
    public PaymentRepository(EntityManager manager) {
        super(manager, Payment.class);
    }

    public List<Payment> findAllByReceiverId(Long receiverId) {
        return new JPAQuery<Payment>(getManager())
                .select(payment)
                .from(payment)
                .where(payment.receiver.id.eq(receiverId))
                .fetch();
    }
}
