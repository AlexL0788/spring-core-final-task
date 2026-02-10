package lomtev.dev.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionHelper {
    private final SessionFactory sessionFactory;
    private static final ThreadLocal<Session> currentSession = new ThreadLocal<>();
    private static final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();


    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void executeInTransaction(Consumer<Session> action) {
        if (currentTransaction.get() != null && currentTransaction.get().isActive()) {
            Session session = currentSession.get();
            action.accept(session);
        } else {
            Transaction tx = null;
            Session session = null;
            try {
                session = sessionFactory.openSession();
                currentSession.set(session);
                tx = session.beginTransaction();
                currentTransaction.set(tx);

                action.accept(session);

                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    try {
                        tx.rollback();
                    } catch (Exception rollbackEx) {
                        e.addSuppressed(rollbackEx);
                    }
                }
                throw e;
            } finally {
                currentTransaction.remove();
                currentSession.remove();
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        }
    }

    public <T> T executeInTransaction(Function<Session, T> action) {
        if (currentTransaction.get() != null && currentTransaction.get().isActive()) {
            Session session = currentSession.get();
            return action.apply(session);
        } else {
            Transaction tx = null;
            Session session = null;
            try {
                session = sessionFactory.openSession();
                currentSession.set(session);
                tx = session.beginTransaction();
                currentTransaction.set(tx);

                T result = action.apply(session);

                tx.commit();
                return result;
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    try {
                        tx.rollback();
                    } catch (Exception rollbackEx) {
                        e.addSuppressed(rollbackEx);
                    }
                }
                throw e;
            } finally {
                currentTransaction.remove();
                currentSession.remove();
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        }
    }
}
