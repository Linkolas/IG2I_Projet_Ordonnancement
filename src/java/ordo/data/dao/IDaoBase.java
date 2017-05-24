package ordo.data.dao;

import java.util.Collection;

/**
 * Describes a basic dao object.
 * 
 * @author Nicolas
 * @param <T> The Entity class which this Dao is directed to.
 */
public interface IDaoBase<T> {
    
    /**
     * Persist an Entity.
     * 
     * @param obj The Entity to be saved.
     * @return Success.
     */
    public boolean create(T obj);
    
    /**
     * Search for an Entity with the set id.
     * @param id The id of the Entity to search for.
     * @return The Entity if found, null if not found.
     */
    public T find(long id);
    
    /**
     * Retrieve a collection of all the Entities.
     * @return A collection of all the existing Entities.
     */
    public Collection<T> findAll();
    
    /**
     * Update an Entity based on its id.
     * @param obj The Entity to merge to the one persisted.
     * @return Success.
     */
    public boolean update(T obj);
    
    /**
     * Delete an Entity based on its id.
     * @param obj The Entity to delete from persistence.
     * @return Success.
     */
    public boolean delete(T obj);
    
    /**
     * Delete every Entity from persitence.
     * @return Success.
     */
    public boolean deleteAll();
    
    /**
     * Close the Dao and make sure it's no more active.
     */
    public void close();
}