/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao.jpa;

import java.util.List;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import ordo.data.entities.*;

/**
 *
 * @author Nicolas
 */
public class JpaLieuDao extends JpaDao<Lieu> {
    
    
    private static JpaLieuDao instance = null;
    
    public static JpaLieuDao getInstance() {
        if (instance == null) {
            instance = new JpaLieuDao();
        }
        
        return instance;
    }
    
    public Lieu findLieuByCoordonnees(float coordX, float coordY)
    {
        Query query = this.em.createNamedQuery("Lieu.findByCoordonnees");
        query.setParameter("coordX", coordX);
        query.setParameter("coordY", coordY);
        
        Lieu lieu = (Lieu) query.getSingleResult();
        
        if(lieu == null)
        {
            System.out.println("Il n'y a pas de lieu correspondant à ces coordonnées");
        }
        
        return lieu;
    }
    
    private JpaLieuDao() {
        super(Lieu.class);
    }
    
}
