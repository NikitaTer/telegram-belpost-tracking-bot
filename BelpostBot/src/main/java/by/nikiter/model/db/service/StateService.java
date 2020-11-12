package by.nikiter.model.db.service;

import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.entity.StateEntity;
import org.hibernate.Session;

import java.util.List;

public class StateService {

    private final StateDao dao;

    public StateService(Session session) {
        dao = new StateDao();
        dao.setSession(session);
    }

    public StateEntity findById(int id) {
        return dao.findById(id);
    }

    public List<StateEntity> findAllStates() {
        return dao.findAll();
    }

    public void addState(StateEntity state) {
        dao.save(state);
    }

    public void updateState(StateEntity state) {
        dao.update(state);
    }

    public void deleteState(StateEntity state) {
        dao.delete(state);
    }
}
