package ru.em.tms.model.db;

import java.io.Serializable;

public interface IEntity<K extends Serializable> {
    K getId();
    void setId(K id);
}
