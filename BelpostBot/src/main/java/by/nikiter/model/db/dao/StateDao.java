package by.nikiter.model.db.dao;

import by.nikiter.model.db.SessionManager;
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

    private final Session session;

    public StateDao(Session session) {
        this.session = session;
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
        session.saveOrUpdate(state);
    }

    @Override
    public void delete(StateEntity state) {
        session.delete(state);
    }

    @Override
    public void deleteById(Integer id) {
        StateEntity state = session.find(StateEntity.class, id);
        if (state != null) {
            session.delete(state);
        }
    }

    @Override
    public StateEntity merge(StateEntity state) {
        return (StateEntity) session.merge(state);
    }
}
