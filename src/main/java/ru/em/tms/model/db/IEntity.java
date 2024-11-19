package ru.em.tms.model.db;

public interface IEntity<K> {
    K getId();
    void setId(K id);
}
