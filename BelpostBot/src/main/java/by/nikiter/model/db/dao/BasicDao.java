package by.nikiter.model.db.dao;

import java.util.List;

public interface BasicDao<T, Id> {
    T findById(Id id);
    List<T> findAll();
    void saveOrUpdate(T entity);
    void delete(T entity);
    void deleteById(Id id);
    T merge(T entity);
}
