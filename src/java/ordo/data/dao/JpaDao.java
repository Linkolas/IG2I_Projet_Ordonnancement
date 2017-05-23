/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

/**
 *
 * @author Nicolas
 */
public abstract class JpaDao<T> implements DaoBase<T> {

    EntityManagerFactory emf = null;
    EntityManager em = null;
    EntityTransaction et = null;
    
    Class<T> classe;
    String entityTableName;
    
    public JpaDao(Class<T> c) {
        classe = c;
        
        if(null != c.getAnnotation(Table.class)) {
            entityTableName = c.getAnnotation(Table.class).name();
        } else {
            entityTableName = c.getSimpleName();
        }
        
        emf = Persistence.createEntityManagerFactory("ProjetOrdonnancementPU");
        em = emf.createEntityManager();
        et = em.getTransaction();
        
        ServletContextListener.addJpaDao(this);
    }
    
    @Override
    public boolean create(T obj) {
        boolean retour = true;
        
        try {
            et.begin();
            em.persist(obj);
            et.commit();
            
        } catch (Exception ex) {
            et.rollback();
            retour = false;
        }
        
        return retour;
    }

    @Override
    public T find(Integer id) {
        return em.find(classe, id);
    }

    @Override
    public Collection<T> findAll() {
        Query createQuery = em.createQuery("select t from " + entityTableName + " t");
        return createQuery.getResultList();
    }

    @Override
    public boolean update(T obj) {
        boolean retour = true;
        
        try {
            et.begin();
            em.merge(obj);
            et.commit();
            
        } catch (Exception ex) {
            retour = false;
        }
        
        return retour;
    }

    @Override
    public boolean delete(T obj) {
        boolean retour = true;
        
        try {
            et.begin();
            em.remove(obj);
            et.commit();
            
        } catch (Exception ex) {
            retour = false;
        }
        
        return retour;
    }

    @Override
    public boolean deleteAll() {
        boolean retour = true;
        
        try {
            et.begin();
            Query createQuery = em.createQuery("delete from " + entityTableName);
            createQuery.executeUpdate();
            et.commit();
            
        } catch (Exception ex) {
            retour = false;
        }
        
        return retour;
    }

    @Override
    public void close() {
        System.out.println("Closing DAO");
        em.close();
        emf.close();
    }
    
}
