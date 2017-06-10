/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao.jpa;

import java.util.Collection;
import java.util.List;
import javax.persistence.Query;
import ordo.data.entities.*;

/**
 *
 * @author Nicolas
 */
public class JpaCommandeClientDao extends JpaDao<CommandeClient> {
    
    
    private static JpaCommandeClientDao instance = null;
    
    public static JpaCommandeClientDao getInstance() {
        if (instance == null) {
            instance = new JpaCommandeClientDao();
        }
        
        return instance;
    }
    
    private JpaCommandeClientDao() {
        super(CommandeClient.class);
    }
    
    /**
     * Get the list of all the CommandeClient pending : 
     * isLivree = false and quantity > 0.
     * @return 
     */
    @Override
    public Collection<CommandeClient> findAll() {
        return findAll(true);
    }
    
    /**
     * 
     * @param pending Whether get only the pending orders or all of them.
     * @return 
     */
    public Collection<CommandeClient> findAll(boolean pending) {
        if(!pending) {
            return super.findAll();
        }
        
        Query createQuery = em.createQuery("select t from CommandeClient t where t.isLivree != true and t.quantiteVoulue > 0");
        return createQuery.getResultList();
    }
    
    public boolean resetCommandes()
    {
        List<CommandeClient> commandes = (List<CommandeClient>) this.findAll();
        
        for(CommandeClient commande: commandes)
        {
            commande.setQuantiteVoulue(0);
        }
        
        return true;
    }
}
