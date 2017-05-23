/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.dao;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextEvent;

/**
 *
 * @author Nicolas
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {
 
    static private List<JpaDao> jpaDaos = new ArrayList<>();
    
    static public void addJpaDao(JpaDao jpaDao) {
        jpaDaos.add(jpaDao);
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("SERVLET CONTEXT INITIALIZED");
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("SERVLET CONTEXT DESTROYED");
        
        for(JpaDao jpaDao: jpaDaos) {
            jpaDao.close();
        }
    }
}
