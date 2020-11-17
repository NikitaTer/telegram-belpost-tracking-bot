package by.nikiter.model.db.dao;

import by.nikiter.model.db.entity.UserEntity;
import org.hibernate.Session;

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
    public void saveOrUpdate(UserEntity user) {
        session.beginTransaction();
        session.saveOrUpdate(user);
        session.getTransaction().commit();
    }

    @Override
    public void delete(UserEntity user) {
        session.beginTransaction();
        user.removeAllTrackings();
        session.delete(user);
        session.getTransaction().commit();
    }

    @Override
    public void deleteById(String username) {
        UserEntity user = session.find(UserEntity.class, username);
        if (user != null) {
            session.beginTransaction();
            user.removeAllTrackings();
            session.delete(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public UserEntity merge(UserEntity user) {
        session.beginTransaction();
        UserEntity userEntity = (UserEntity) session.merge(user);
        session.getTransaction().commit();
        return userEntity;
    }
}
