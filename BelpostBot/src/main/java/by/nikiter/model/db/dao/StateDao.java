package by.nikiter.model.db.dao;

import by.nikiter.model.db.entity.StateEntity;
import org.hibernate.Session;

import java.util.List;

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
    public void save(StateEntity state) {
        session.beginTransaction();
        session.save(state);
        session.getTransaction().commit();
    }

    @Override
    public void update(StateEntity state) {
        session.beginTransaction();
        session.update(state);
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
        session.beginTransaction();
        StateEntity state = session.find(StateEntity.class, id);
        session.delete(state);
        session.getTransaction().commit();
    }

    @Override
    public void merge(StateEntity state) {
        session.beginTransaction();
        session.merge(state);
        session.getTransaction().commit();
    }
}
