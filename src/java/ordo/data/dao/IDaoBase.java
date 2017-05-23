/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao;

import java.util.Collection;

public interface IDaoBase<T> {
    public boolean create(T obj);
    public T find(Integer id);
    public Collection<T> findAll();
    public boolean update(T obj);
    public boolean delete(T obj);
    public boolean deleteAll();
    public void close();
}