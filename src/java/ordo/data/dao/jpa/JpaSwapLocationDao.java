/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao.jpa;


import ordo.data.entities.SwapLocation;

/**
 *
 * @author Axelle
 */
public class JpaSwapLocationDao extends JpaDao<SwapLocation>
{
    private static JpaSwapLocationDao instance = null;
    
    public static JpaSwapLocationDao getInstance() {
        if (instance == null) {
            instance = new JpaSwapLocationDao();
        }
        
        return instance;
    }
    
    private JpaSwapLocationDao() {
        super(SwapLocation.class);
    }
}
