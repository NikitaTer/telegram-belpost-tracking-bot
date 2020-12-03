package by.nikiter.model.db.dao;

import by.nikiter.model.db.entity.UserEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * Class that provides dao methods that uses to interact with user table
 *
 * @see UserEntity
 * @see UserTrackingEntity
 * @author NikiTer
 */
public class UserDao implements BasicDao<UserEntity,String> {

    private final Session session;

    public UserDao(Session session) {
        this.session = session;
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
        session.saveOrUpdate(user);
    }

    @Override
    public void delete(UserEntity user) {
        user.removeAllTrackings();
        session.delete(user);
    }

    @Override
    public void deleteById(String username) {
        UserEntity user = session.find(UserEntity.class, username);
        if (user != null) {
            user.removeAllTrackings();
            session.delete(user);
        }
    }

    @Override
    public UserEntity merge(UserEntity user) {
        return (UserEntity) session.merge(user);
    }
}
