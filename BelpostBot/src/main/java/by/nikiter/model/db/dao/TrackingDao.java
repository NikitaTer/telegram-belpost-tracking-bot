package by.nikiter.model.db.dao;

import by.nikiter.model.db.HibernateUtil;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserEntity;
import org.hibernate.Session;

import java.util.List;

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
    public void save(TrackingEntity tracking) {
        session.beginTransaction();
        session.save(tracking);
        session.getTransaction().commit();
    }

    @Override
    public void update(TrackingEntity tracking) {
        session.beginTransaction();
        session.update(tracking);
        session.getTransaction().commit();
    }

    @Override
    public void delete(TrackingEntity tracking) {
        session.beginTransaction();
        for (UserEntity user : tracking.getUsers()) {
            tracking.removeUser(user);
        }
        session.delete(tracking);
        session.getTransaction().commit();
    }

    @Override
    public void deleteById(String number) {
        session.beginTransaction();
        TrackingEntity tracking = session.find(TrackingEntity.class, number);
        for (UserEntity user : tracking.getUsers()) {
            tracking.removeUser(user);
        }
        session.delete(tracking);
        session.getTransaction().commit();
    }

    @Override
    public void merge(TrackingEntity tracking) {
        session.beginTransaction();
        session.merge(tracking);
        session.getTransaction().commit();
    }
}
