package by.nikiter.model.db.dao;

import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserEntity;
import org.hibernate.Session;

import java.util.Iterator;
import java.util.List;

public class UserDao implements BasicDao<UserEntity,String> {

    private Session session = null;

    public UserDao() {
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public UserEntity findById(String username) {
        return session.find(UserEntity.class, username);
    }

    @Override
    public List<UserEntity> findAll() {
        return (List<UserEntity>)session.createQuery("From UserEntity").list();
    }

    @Override
    public void save(UserEntity user) {
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }

    @Override
    public void update(UserEntity user) {
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
    }

    @Override
    public void delete(UserEntity user) {
        session.beginTransaction();
        for (TrackingEntity tracking : user.getTrackings()) {
            user.removeTracking(tracking);
        }
        session.delete(user);
        session.getTransaction().commit();
    }

    @Override
    public void deleteById(String username) {
        session.beginTransaction();
        UserEntity user = session.find(UserEntity.class, username);

        user.removeAllTrackings();

        session.delete(user);
        session.getTransaction().commit();
    }

    @Override
    public void merge(UserEntity user) {
        session.beginTransaction();
        session.merge(user);
        session.getTransaction().commit();
    }
}
