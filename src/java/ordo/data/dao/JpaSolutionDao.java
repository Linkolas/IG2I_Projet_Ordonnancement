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
public class JpaSolutionDao extends JpaDao<Solution> {
    
    
    private static JpaSolutionDao instance = null;
    
    public static JpaSolutionDao getInstance() {
        if (instance == null) {
            instance = new JpaSolutionDao();
        }
        
        return instance;
    }
    
    private JpaSolutionDao() {
        super(Solution.class);
    }
    
}
