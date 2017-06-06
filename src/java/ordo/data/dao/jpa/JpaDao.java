package ordo.data.dao.jpa;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;
import ordo.context.ServletContextListener;
import ordo.data.dao.IDaoBase;

/**
 * JPA implementation of IDaoBase.
 * Extend this class when creating a new entity model to persist.
 * 
 * @author Nicolas
 */
public abstract class JpaDao<T> implements IDaoBase<T> {

    /** The EMF must be closed when the app does not need it anymore. */
    private EntityManagerFactory emf = null;
    /** Used to interact with the persistence context. */
    protected EntityManager em = null;
    /** Used to control transactions with persistence context. */
    protected EntityTransaction et = null;
    
    /** The class of the entity wich this JPA is devoted to. */
    protected Class<T> entityClass;
    /** The Table in which the Entity is stored. */
    protected String entityTableName;
    
    /**
     * Initialize the JpaDao object for the given Entity class.
     * @param entityClass The Entity class for which this JpaDao is created.
     */
    public JpaDao(Class<T> entityClass) {
        // Save the Entity data : its class and its table name.
        this.entityClass = entityClass;
        
        // We have to check if a @Table annotation has been used.
        if(null != this.entityClass.getAnnotation(Table.class)) {
            // If it has, table the specified name.
            entityTableName = this.entityClass.getAnnotation(Table.class).name();
        } else {
            // If not, default to the class name.
            entityTableName = this.entityClass.getSimpleName();
        }
        
        // Create the Persistence environment for this entity class.
        emf = Persistence.createEntityManagerFactory("ProjetOrdonnancementPU");
        em = emf.createEntityManager();
        et = em.getTransaction();
        
        // Register this Dao so it gets closed when needed.
        // FIXME: This should not be done here, but in a Factory.
        ServletContextListener.registerDao(this);
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
    
    public boolean create(List<T> objList)
    {
        int i=0;
        try
        {
            et.begin();
            for(T obj: objList)
            {
                System.out.println(i++);
                em.persist(obj);
            }
            et.commit();
        }
        catch (Exception ex) 
        {
            et.rollback();
            return false;
        }
        
        return true;
    }
    
    @Override
    public T find(long id) {
        return em.find(entityClass, id);
    }

    @Override
    public Collection<T> findAll() {
        //Query createQuery = em.createQuery("select t from " + entityTableName + " t");
        Query createQuery = em.createQuery("select t from " + entityClass.getSimpleName() + " t");
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
            Query createQuery = em.createQuery("delete from " + entityClass.getSimpleName());
            createQuery.executeUpdate();
            et.commit();
            
        } catch (Exception ex) {
            retour = false;
        }
        
        return retour;
    }

    @Override
    public void close() {
        System.out.println("Closing DAO for entity " + entityClass.getSimpleName());
        em.close();
        emf.close();
    }
    
}
