package ordo.context;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextEvent;
import ordo.data.dao.IDaoBase;

/**
 * This class is registered as a Listener in the web.xml file.
 * It is called at app deployment and un-deployment.
 * 
 * Its purpose is to make sure that all the IDaoBase objects are closed;
 * if it's not the case, the re-deployed app could have conflicts where
 * two conflicting versions may be in memory.
 * 
 * @author Nicolas
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {
 
    /** List of the registered IDaoBase objects to close. */
    static private List<IDaoBase> daos = new ArrayList<>();
    
    /**
     * Registers a IDaoBase object so it is closed whenever the app is
     * un-deployed.
     * @param dao 
     */
    static public void registerDao(IDaoBase dao) {
        daos.add(dao);
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("SERVLET CONTEXT INITIALIZED");
    }
 
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("SERVLET CONTEXT DESTROYED");
        
        // Close all the registered IDaoBase objects.
        for(IDaoBase dao: daos) {
            dao.close();
        }
    }
}
