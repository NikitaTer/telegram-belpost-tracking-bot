package by.nikiter.model.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Util that build and contains {@link SessionFactory}
 *
 * @author NikiTer
 */
public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            SessionFactory sf = new Configuration().configure().buildSessionFactory();
            logger.info("Session factory built");
            return sf;
        } catch (HibernateException ex) {
            logger.error("No access to database: " + ex);
            throw new RuntimeException("No access to database: " + ex);
        }
    }

    public static synchronized SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static synchronized void close() {
        sessionFactory.close();
    }
}