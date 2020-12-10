package by.nikiter.model.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * Util that provides interface to work with {@link HibernateUtil} and also contains {@link Session}
 *
 * @author NikiTer
 */
public class SessionManager {
    private static final Logger logger = LogManager.getLogger(SessionManager.class);

    private Session session = null;

    public SessionManager() {
    }

    public void openSession() {
        if (session == null) {
            try {
                session = HibernateUtil.getSessionFactory().openSession();
                logger.info("Session opened");
            } catch (HibernateException ex) {
                logger.error("Session cannot open: " + ex.getMessage());
            }
        } else {
            logger.error("More then one session");
            throw new RuntimeException("More then one session");
        }
    }

    public void closeSession() {
        if (session != null) {
            try {
                session.close();
                logger.info("Session closed ");
                session = null;
            } catch (HibernateException ex) {
                logger.error("Session cannot close");
            }
        } else {
            logger.error("No active session");
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
                logger.info("Transaction began");
            } else {
                logger.error("More then one active transaction");
                throw new RuntimeException("More then one active transaction");
            }
        } else {
            logger.error("No active session");
            throw new RuntimeException("No active session");
        }
    }

    public void commitTransaction() {
        if (session != null) {
            if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE) {
                session.getTransaction().commit();
                logger.info("Transaction committed");
            } else {
                logger.error("Attempt to commit not active transaction");
                throw new RuntimeException("Attempt to commit not active transaction");
            }
        } else {
            logger.error("No active session");
            throw new RuntimeException("No active session");
        }
    }

    public void rollback() {
        if (session != null) {
            if (session.getTransaction().getStatus() != TransactionStatus.NOT_ACTIVE &&
                    session.getTransaction().getStatus() != TransactionStatus.COMMITTED) {
                session.getTransaction().rollback();
                logger.info("Transaction rolled back");
            }
        } else {
            logger.error("No active session");
            throw new RuntimeException("No active session");
        }
    }

    public void detach(Object o) {
        session.detach(o);
        logger.info("Detached object " + o.toString());
    }

    public void flush() {
        if (session != null) {
            try {
                session.flush();
                logger.info("Session flushed");
            } catch (HibernateException ex) {
                logger.error("Session cannot flushed");
            }
        } else {
            logger.error("No active session");
            throw new RuntimeException("No active session");
        }
    }

    public void clear() {
        if (session != null) {
            session.clear();
            logger.info("Session cleared");
        } else {
            logger.error("No active session");
            throw new RuntimeException("No active session");
        }
    }

    public <T> Query<T> createQuery(String statement, Class<T> aClass) {
        return session.createQuery(statement,aClass);
    }
}