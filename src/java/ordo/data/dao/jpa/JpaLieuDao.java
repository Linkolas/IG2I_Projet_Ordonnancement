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
public class JpaLieuDao extends JpaDao<Lieu> {
    
    
    private static JpaLieuDao instance = null;
    
    public static JpaLieuDao getInstance() {
        if (instance == null) {
            instance = new JpaLieuDao();
        }
        
        return instance;
    }
    
    private JpaLieuDao() {
        super(Lieu.class);
    }
    
}
