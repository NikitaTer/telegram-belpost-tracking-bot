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

    private final Session session;

    public TrackingDao(Session session) {
        this.session = session;
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
        session.saveOrUpdate(tracking);
    }

    @Override
    public void delete(TrackingEntity tracking) {
        tracking.removeAllUsers();
        session.delete(tracking);
    }

    @Override
    public void deleteById(String number) {
        TrackingEntity tracking = session.find(TrackingEntity.class, number);
        if (tracking != null) {
            tracking.removeAllUsers();
            session.delete(tracking);
        }
    }

    @Override
    public TrackingEntity merge(TrackingEntity tracking) {
        return (TrackingEntity) session.merge(tracking);
    }
}
