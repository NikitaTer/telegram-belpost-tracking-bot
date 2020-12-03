package by.nikiter.model.db.service;

import by.nikiter.model.db.SessionManager;
import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.entity.StateEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * Service that provides an interface for working with state table through {@link StateDao}
 *
 * @see StateDao
 * @author NikiTer
 */
public class StateService {

    private final StateDao dao;

    private final SessionManager sessionManager;

    public StateService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        dao = new StateDao(sessionManager.getSession());
    }

    public StateEntity findById(int id) {
        return dao.findById(id);
    }

    public List<StateEntity> findAllStates() {
        return dao.findAll();
    }

    public void addState(StateEntity state) {
        dao.saveOrUpdate(state);
    }

    public void updateState(StateEntity state) {
        dao.merge(state);
    }

    public void deleteState(StateEntity state) {
        dao.delete(state);
    }
}
