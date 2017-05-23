/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao.jpa;

import ordo.data.entities.*;

/**
 *
 * @author Nicolas
 */
public class JpaTrajetDao extends JpaDao<Trajet> {
    
    
    private static JpaTrajetDao instance = null;
    
    public static JpaTrajetDao getInstance() {
        if (instance == null) {
            instance = new JpaTrajetDao();
        }
        
        return instance;
    }
    
    private JpaTrajetDao() {
        super(Trajet.class);
    }
    
}
