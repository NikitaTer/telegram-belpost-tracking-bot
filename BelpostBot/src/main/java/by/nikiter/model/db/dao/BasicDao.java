package by.nikiter.model.db.dao;

import java.util.List;

/**
 * Interface that have basic methods for dao
 *
 * @param <T> Class of table entity
 * @param <Id> Type of id field
 *
 * @author NikiTer
 */
public interface BasicDao<T, Id> {
    T findById(Id id);
    List<T> findAll();
    void saveOrUpdate(T entity);
    void delete(T entity);
    void deleteById(Id id);
    T merge(T entity);
}
