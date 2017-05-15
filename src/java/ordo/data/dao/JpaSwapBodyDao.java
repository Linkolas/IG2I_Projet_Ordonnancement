/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao;

import ordo.data.entities.*;

/**
 *
 * @author Nicolas
 */
public class JpaSwapBodyDao extends JpaDao<SwapBody> {
    
    
    private static JpaSwapBodyDao instance = null;
    
    public static JpaSwapBodyDao getInstance() {
        if (instance == null) {
            instance = new JpaSwapBodyDao();
        }
        
        return instance;
    }
    
    private JpaSwapBodyDao() {
        super(SwapBody.class);
    }
    
}
