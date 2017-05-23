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
public class JpaDepotDao extends JpaDao<Depot> {
    
    
    private static JpaDepotDao instance = null;
    
    public static JpaDepotDao getInstance() {
        if (instance == null) {
            instance = new JpaDepotDao();
        }
        
        return instance;
    }
    
    private JpaDepotDao() {
        super(Depot.class);
    }
    
}
