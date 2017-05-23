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
    
}
