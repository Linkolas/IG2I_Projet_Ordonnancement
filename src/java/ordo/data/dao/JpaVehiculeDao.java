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
public class JpaVehiculeDao extends JpaDao<Vehicule> {
    
    
    private static JpaVehiculeDao instance = null;
    
    public static JpaVehiculeDao getInstance() {
        if (instance == null) {
            instance = new JpaVehiculeDao();
        }
        
        return instance;
    }
    
    private JpaVehiculeDao() {
        super(Vehicule.class);
    }
    
}
