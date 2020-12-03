package by.nikiter.model.db;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * Util that provides interface to work with {@link HibernateUtil} and also contains {@link Session}
 *
 * @author NikiTer
 */
public class SessionManager {

    private Session session = null;

    public SessionManager() {
    }

    public void openSession() {
        if (session == null) {
            session = HibernateUtil.getSessionFactory().openSession();
        } else {
            throw new RuntimeException("More then one session");
        }
    }

    public void closeSession() {
        if (session != null) {
            session.close();
            session = null;
        } else {
            throw new RuntimeException("No active session");
        }
    }

    public Session getSession() {
        return session;
    }

    public void beginTransaction() {
        if (session != null) {
            if (session.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
                session.beginTransaction();
            } else {
                throw new RuntimeException("More then one active transaction");
            }
        } else {
            throw new RuntimeException("No active session");
        }
    }

    public void commitTransaction() {
        if (session != null) {
            if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE) {
                session.getTransaction().commit();
            } else {
                throw new RuntimeException("Attempt to commit not active transaction");
            }
        } else {
            throw new RuntimeException("No active session");
        }
    }

    public void rollback() {
        if (session != null) {
            if (session.getTransaction().getStatus() != TransactionStatus.NOT_ACTIVE &&
                    session.getTransaction().getStatus() != TransactionStatus.COMMITTED) {
                session.getTransaction().rollback();
            }
        } else {
            throw new RuntimeException("No active session");
        }
    }

    public void detach(Object o) {
        session.detach(o);
    }

    public void flush() {
        if (session != null) {
            session.flush();
        } else {
            throw new RuntimeException("No active session");
        }
    }

    public void clear() {
        if (session != null) {
            session.clear();
        } else {
            throw new RuntimeException("No active session");
        }
    }

    public <T> Query<T> createQuery(String statement, Class<T> aClass) {
        return session.createQuery(statement,aClass);
    }
}