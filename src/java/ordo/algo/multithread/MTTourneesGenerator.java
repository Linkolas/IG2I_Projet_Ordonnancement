/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import static ordo.algo.AlgoRandom.choisirCommandeRandom;
import ordo.algo.HypoTournee;
import ordo.data.Constantes;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Trajet;

/**
 *
 * @author Nicolas
 */
public class MTTourneesGenerator implements Callable<MTTGResults> {
    
    public static final int SOUT_EVERY = 15; // seconds
    
    
    private HashMap<MTTrajetKey, Trajet> trajets = null;
    private Collection<CommandeClient> clientsTrains  = null;
    private Collection<CommandeClient> clientsCamions = null;
    private Collection<CommandeClient> clientsAll     = null;
    private Depot depot = null;
    private static int THREAD_NUMBER = 1;
    private int threadNumber = 0;
    
    public MTTourneesGenerator() {
        threadNumber = THREAD_NUMBER++;
    }
    
    /** Number of seconds to run the generation. */
    private long runtime = 10;

    public HashMap<MTTrajetKey, Trajet> getTrajets() {
        return trajets;
    }

    public void setTrajets(HashMap<MTTrajetKey, Trajet> trajets) {
        this.trajets = trajets;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public Collection<CommandeClient> getClientsTrains() {
        return clientsTrains;
    }

    public void setClientsTrains(Collection<CommandeClient> clientsTrains) {
        this.clientsTrains = clientsTrains;
    }

    public Collection<CommandeClient> getClientsCamions() {
        return clientsCamions;
    }

    public void setClientsCamions(Collection<CommandeClient> clientsCamions) {
        this.clientsCamions = clientsCamions;
    }

    public Collection<CommandeClient> getClientsAll() {
        return clientsAll;
    }

    public void setClientsAll(Collection<CommandeClient> clientsAll) {
        this.clientsAll = clientsAll;
    }
    
    public long getRuntime() {
        return runtime;
    }

    /**
     * Set the amount of time during which it generates the Tournees.
     * @param runtime Number of seconds.
     */
    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    @Override
    public MTTGResults call() {
        MTTGResults results = new MTTGResults();

        if(trajets == null || clientsAll == null || depot == null
                || clientsCamions == null || clientsTrains == null) {
            results.setMessage("Missing data.");
            return results;
        }
        
        System.out.println(threadNumber + " | " + "TRAJETS : " + trajets.size());
        System.out.println(threadNumber + " | " + "CLIENTS : " + clientsAll.size());
        System.out.println(threadNumber + " | " + "CLIENTS C : " + clientsCamions.size());
        System.out.println(threadNumber + " | " + "CLIENTS T : " + clientsTrains.size());
        
        Set<MTTournee> tournees = new HashSet<>();
        Set<MTSolution> solutions = new HashSet<>();
        
        long beginTime = System.currentTimeMillis();
        long timeout = beginTime + (runtime * 1000);
        long lastSout = System.currentTimeMillis();
        while(System.currentTimeMillis() < timeout) {
            
            //Liste des clients restants pour la SOLUTION
            List<CommandeClient> clientsRestants = new ArrayList<>(clientsAll);
            List<CommandeClient> clientsRestantsTrains = new ArrayList<>(clientsTrains);
            MTSolution solution = new MTSolution();
            
            while(clientsRestants.size() > 0) {
                CommandeClient client = clientsRestants.get(0); // TODO: aléatoire ?
                CommandeClient lastInserted;
                
                // La tournee en cours.
                MTTournee tournee;
                
                if(client.getQuantiteVoulue() <= Constantes.capaciteMax) {
                    tournee = new MTTournee(MTTournee.Type.CAMION, trajets);
                    //clientsRestants.addAll(clientsCamions);
                } else {
                    tournee = new MTTournee(MTTournee.Type.TRAIN, trajets);
                    //clientsRestants.addAll(clientsTrains);
                }

                // On part du dépot
                tournee.addLieu(depot);
                // On va chez le client
                tournee.addLieu(client);
                lastInserted = client;
                // On revient de chez le client
                tournee.addLieu(depot);

                // On enlève le client en cours des choix aléatoire
                clientsRestants.remove(client);
                clientsRestantsTrains.remove(client);

                
                int retry = 3;
                while(retry > 0) {
                    // Randomisation = ajout d'un client aléatoire
                    CommandeClient nouveauClient;
                    if(tournee.getType() == MTTournee.Type.CAMION) {
                        
                        if(clientsRestants.size() < 1) {
                            retry = 0;
                            break;
                        }
                        
                        nouveauClient = choisirCommandeRandom(clientsRestants);
                        
                    } else {
                        
                        if(clientsRestantsTrains.size() < 1) {
                            retry = 0;
                            break;
                        }
                        
                        nouveauClient = choisirCommandeRandom(clientsRestantsTrains);
                    }

                    // On ajoute le nouveau client à la tournée
                    tournee.addLieuBestInsertion(nouveauClient);
                    lastInserted = nouveauClient;
                    
                    if(tournee.isTooFull() && !tournee.isTooLong()) {
                        // Si c'est une tournée CAMION mais qu'on ne livre pas de camion
                        if(tournee.getType() == MTTournee.Type.CAMION && !tournee.containsClientCamion()) {
                            MTTournee saved = new MTTournee(tournee, trajets);
                            tournees.add(saved);
                        }
                    }
                    
                    if(/*!tournee.isTooFull() &&*/ tournee.isTooLong()) {
                        // TODO : Reorganiser l'ordre de passage (code des profs)
                    }
                    
                    if(tournee.isTooFull() || tournee.isTooLong()) {
                        
                        tournee.removeLieu(lastInserted);
                        retry--;
                        
                    } else {
                        // On retire ce client des possibilités pour le prochain tirage
                        clientsRestants.remove(nouveauClient);
                        clientsRestantsTrains.remove(nouveauClient);
                    }
                }

                // Il faut appeller getCost pour que le coût soit mis à jour.
                tournee.getCost();
                //System.out.println(tournee);

                // Enfin, on l'ajoute à la liste.
                tournees.add(tournee);
                solution.tournees.add(tournee);
            }
            
            solutions.add(solution);
            
            long elapsed = (System.currentTimeMillis() - beginTime)/1000;
            
            if(System.currentTimeMillis() > lastSout + SOUT_EVERY*1000) {
                lastSout = System.currentTimeMillis();
                System.out.println(threadNumber + " | " + elapsed + "/" + runtime + "s : " + tournees.size() + " tournées hypothétiques générées !");
            }
        }
        
        results.setSolutions(solutions);
        results.setTournees(tournees);
        return results;
    }

    /**
     * Permet de choisir une commandeClient de manière aléatoire dans une liste de clients
     * @param clients La liste des clients parmi lesquels on peut piocher
     * @return Retourne une CommandeClient au hasard
     */
    public CommandeClient choisirCommandeRandom(List<CommandeClient> clients) {
        Random randomGenerator = new Random();
        int randomIndex = randomGenerator.nextInt(clients.size());
        CommandeClient commandeRandom = clients.get(randomIndex);
        return commandeRandom;
    }
}