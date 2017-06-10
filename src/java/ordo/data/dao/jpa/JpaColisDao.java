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
public class JpaColisDao extends JpaDao<Colis> {
    
    
    private static JpaColisDao instance = null;
    
    public static JpaColisDao getInstance() {
        if (instance == null) {
            instance = new JpaColisDao();
        }
        
        return instance;
    }
    
    private JpaColisDao() {
        super(Colis.class);
    }
    
}
