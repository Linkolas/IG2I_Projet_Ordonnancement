/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao.jpa;

import java.util.List;
import javax.persistence.Query;
import ordo.data.entities.*;

/**
 *
 * @author Nicolas
 */
public class JpaVehiculeActionDao extends JpaDao<VehiculeAction> {
    
    
    private static JpaVehiculeActionDao instance = null;
    
    public static JpaVehiculeActionDao getInstance() {
        if (instance == null) {
            instance = new JpaVehiculeActionDao();
        }
        
        return instance;
    }
    
    private JpaVehiculeActionDao() {
        super(VehiculeAction.class);
    }
    
    public List<VehiculeAction> findByVehicule(Vehicule vehicule)
    {
        Query query = this.em.createNamedQuery("VehiculeAction.findByVehicule");
        query.setParameter("vehicule", vehicule);   
        
        List<VehiculeAction> vehiculeActions = query.getResultList();
        
        if(vehiculeActions == null)
        {
            System.out.println("Il n'y a pas de liste d'actions lié à ce véhicule");
        }
        
        return vehiculeActions;
    }
}
