package by.nikiter.model.db.dao;

import by.nikiter.model.db.entity.StateEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * Class that provides dao methods that uses to interact with state table
 *
 * @see StateEntity
 * @see BasicDao
 * @author NikiTer
 */
public class StateDao implements BasicDao<StateEntity, Integer> {

    private Session session = null;

    public StateDao() {
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public StateEntity findById(Integer id) {
        return session.find(StateEntity.class, id);
    }

    @Override
    public List<StateEntity> findAll() {
        return (List<StateEntity>) session.createQuery("From StateEntity").list();
    }

    @Override
    public void saveOrUpdate(StateEntity state) {
        session.beginTransaction();
        session.saveOrUpdate(state);
        session.getTransaction().commit();
    }

    @Override
    public void delete(StateEntity state) {
        session.beginTransaction();
        session.delete(state);
        session.getTransaction().commit();
    }

    @Override
    public void deleteById(Integer id) {
        StateEntity state = session.find(StateEntity.class, id);
        if (state != null) {
            session.beginTransaction();
            session.delete(state);
            session.getTransaction().commit();
        }
    }

    @Override
    public StateEntity merge(StateEntity state) {
        session.beginTransaction();
        StateEntity stateEntity = (StateEntity) session.merge(state);
        session.getTransaction().commit();
        return stateEntity;
    }
}
