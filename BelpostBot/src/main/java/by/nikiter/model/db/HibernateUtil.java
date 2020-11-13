package by.nikiter.model.db;

import by.nikiter.model.db.service.ServiceManager;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new RuntimeException("No access to database: " + ex);
        }
    }

    public static synchronized SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static synchronized void close() {
        sessionFactory.close();
    }

    public static void main(String[] args) {
        ServiceManager manager = new ServiceManager();
        manager.openSession();

        /*manager.getUserService().addUser("Not", 54321);*/

        /*manager.getUserService().addTracking("Not", "12345", "Not's 12345 post");*/

        /*manager.getUserService().removeTracking("Not","54321");
        manager.getTrackingService().tryToDeleteTracking("12345");*/

        manager.getUserService().deleteUser("NikiTer1");

        manager.closeSession();
    }
}