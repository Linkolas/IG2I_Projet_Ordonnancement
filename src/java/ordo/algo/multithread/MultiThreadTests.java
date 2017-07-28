/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import ordo.algo.HypoTournee;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaDepotDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.Trajet;
import ordo.data.metier.CSVReader;

/**
 *
 * @author Nicolas
 */
public class MultiThreadTests {
    
    public int threads = 4;
    public int generateTime = 10;
    
    public MTTGResults runTests() {
        MTTGResults rtn = new MTTGResults();
        Set<MTTournee> tournees = new HashSet<>();
        Set<MTSolution> solutions = new HashSet<>();
        
        try {
            // Create a InitContext instance
            InitialContext context = new InitialContext();
            
            // Obtaining a default ManagedExecutorService
            ManagedExecutorService executorService =
                    (ManagedExecutorService) context.lookup("java:comp/DefaultManagedExecutorService");
            
            
            // Create thread instances
            final List<Callable<MTTGResults>> callables = initializeThreads(threads);
            
            // Call the threads and get their results
            System.out.println("LAUNCHING CALLABLES");
            List<Future<MTTGResults>> results = executorService.invokeAll(callables);
            
            for(Future<MTTGResults> result: results) {
                tournees.addAll(result.get().getTournees());
                solutions.addAll(result.get().getSolutions());
            }
            
            System.out.println("TOTAL OF TOURNEES : " + tournees.size());
            System.out.println("TOTAL OF SOLUTIONS : " + solutions.size());
        } catch (NamingException ex) {
            Logger.getLogger(MultiThreadTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MultiThreadTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(MultiThreadTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        rtn.setTournees(tournees);
        rtn.setSolutions(solutions);
        return rtn;
    }
    
    
    private List<Callable<MTTGResults>> initializeThreads(int numberOfThreads) {
        JpaTrajetDao daoTrajet = JpaTrajetDao.getInstance();
        JpaCommandeClientDao daoCc = JpaCommandeClientDao.getInstance();
        JpaDepotDao daoDepot = JpaDepotDao.getInstance();

        // Retrieve necessary data
        System.out.println("RETRIEVING DATA...");
        Collection<Trajet> trajetsCollection = daoTrajet.findAll();
        Collection<CommandeClient> ccs      = daoCc.findAll(true);
        List<CommandeClient> clientsTrains  = daoCc.findAllTrains();
        List<CommandeClient> clientsCamions = daoCc.findAllCamions();
        Depot depot = daoDepot.findAll().iterator().next();
        
        // Generate the Map of Trajets
        System.out.println("CONVERTING TRAJETS...");
        HashMap<MTTrajetKey, Trajet> trajets = new HashMap<>();
        for(Trajet trajet: trajetsCollection) {
            MTTrajetKey key = new MTTrajetKey(trajet.getDepart(), trajet.getDestination());
            trajets.put(key, trajet);
        }

        System.out.println("TRAJETS : " + trajets.size());
        List<Callable<MTTGResults>> callables = new ArrayList<>();
        
        // Create thread instances
        System.out.println("CREATING CALLABLES...");
        for(int i = 0; i<numberOfThreads; i++) {
            MTTourneesGenerator htg = new MTTourneesGenerator();
            htg.setTrajets(trajets);
            htg.setClientsAll(ccs);
            htg.setClientsCamions(clientsCamions);
            htg.setClientsTrains(clientsTrains);
            htg.setDepot(depot);
            
            htg.setRuntime(generateTime);
            
            callables.add(htg);
        }
        
        return callables;
    }
    
    
    
}
