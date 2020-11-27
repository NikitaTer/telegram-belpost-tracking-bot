package by.nikiter.model.db.dao;

import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * Class that provides dao methods that uses to interact with tracking table
 *
 * @see TrackingEntity
 * @see UserTrackingEntity
 * @see BasicDao
 * @author NikiTer
 */
public class TrackingDao implements BasicDao<TrackingEntity,String> {

    private Session session = null;

    public TrackingDao() {
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public TrackingEntity findById(String number) {
        return session.find(TrackingEntity.class, number);
    }

    @Override
    public List<TrackingEntity> findAll() {
        return (List<TrackingEntity>) session.createQuery("From TrackingEntity").list();
    }

    @Override
    public void saveOrUpdate(TrackingEntity tracking) {
        session.beginTransaction();
        session.saveOrUpdate(tracking);
        session.getTransaction().commit();
    }

    @Override
    public void delete(TrackingEntity tracking) {
        session.beginTransaction();
        tracking.removeAllUsers();
        session.delete(tracking);
        session.getTransaction().commit();
    }

    @Override
    public void deleteById(String number) {
        TrackingEntity tracking = session.find(TrackingEntity.class, number);
        if (tracking != null) {
            session.beginTransaction();
            tracking.removeAllUsers();
            session.delete(tracking);
            session.getTransaction().commit();
        }
    }

    @Override
    public TrackingEntity merge(TrackingEntity tracking) {
        session.beginTransaction();
        TrackingEntity trackingEntity = (TrackingEntity) session.merge(tracking);
        session.getTransaction().commit();
        return trackingEntity;
    }
}
