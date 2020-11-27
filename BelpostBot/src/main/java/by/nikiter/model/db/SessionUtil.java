package by.nikiter.model.db;

import org.hibernate.Session;

/**
 * Util that provides interface to work with {@link HibernateUtil} and also contains {@link Session}
 *
 * @author NikiTer
 */
public class SessionUtil {

    private Session session;

    public SessionUtil() {
    }

    public void openSession() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    public void closeSession() {
        session.close();
    }

    public Session getSession() {
        return session;
    }
}
