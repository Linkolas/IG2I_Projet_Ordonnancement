/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import ordo.cplex.CplexSolve;
import ordo.cplex.CplexTournee;
import ordo.cplex.DeCplexifier;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaDepotDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSolutionDao;
import ordo.data.dao.jpa.JpaSwapBodyDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.dao.jpa.JpaVehiculeActionDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.Trajet;
import ordo.data.metier.CSVReader;
import ordo.data.metier.CSVWriter;

/**
 *
 * @author Axelle
 */
public class AlgoRandom {
    
    public static void testCodePostal(HypoTournee tournee)
    {
        for(Lieu lieu: tournee.getLieux())
        {
            System.out.println(lieu.getCodePostal());
        }
    }
    
    public static Set<HypoTournee> makeTourneesRandom(long timeoutSeconds)
    {
        
        // Dans un premier temps on a besoin de récupérer les instances de chaques objets
        JpaDepotDao             daoDepot            = JpaDepotDao.getInstance();
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        
        List<CommandeClient> clientsTrains  = daoCommandeClient.findAllTrains();
        List<CommandeClient> clientsCamions = daoCommandeClient.findAllCamions();
        List<CommandeClient> clientsAll     = new ArrayList<>(daoCommandeClient.findAll());
        Depot depot = daoDepot.findAll().iterator().next();
        
        Set<HypoTournee> tournees = generateSafeTournees(clientsAll, depot);
        
        long beginTime = System.currentTimeMillis();
        long timeout = beginTime + (timeoutSeconds * 1000);
        
        while(System.currentTimeMillis() < timeout) {
            
            for(CommandeClient client : clientsAll) {

                // La tournee en cours.
                HypoTournee tournee;

                //Liste des clients restants
                List<CommandeClient> clientsRestants = new ArrayList<>();

                if(client.getNombreRemorquesMax() < 2) {
                    tournee = new HypoTournee(HypoTournee.Type.CAMION);
                    clientsRestants.addAll(clientsCamions);
                } else {
                    tournee = new HypoTournee(HypoTournee.Type.TRAIN);
                    clientsRestants.addAll(clientsTrains);
                }

                // On part du dépot
                tournee.addLieu(depot);
                // On va chez le client
                tournee.addLieu(client);

                // On enlève le client en cours des choix aléatoire
                clientsRestants.remove(client);

                while(!tournee.isTooFull() && !tournee.isTooLong()) {
                    // Randomisation = ajout d'un client aléatoire
                    CommandeClient nouveauClient = choisirCommandeRandom(clientsRestants);

                    // On ajoute le nouveau client à la tournée
                    tournee.addLieu(nouveauClient);

                    // On retire ce client des possibilités pour le prochain tirage
                    clientsRestants.remove(nouveauClient);
                }

                // Le dernier lieu ajouté a fait dépasser un limite, on l'enlève
                tournee.removeLastLieu();
                
                // On doit maintenant retourner au dépot
                tournee.addLieu(depot);

                while(tournee.isTooFull() || tournee.isTooLong()) {
                    // On retire le depôt
                    tournee.removeLastLieu();

                    // On retire le dernier lieu ajouté
                    tournee.removeLastLieu();

                    // On ajoute le dépôt
                    tournee.addLieu(depot);  
                }

                // Il faut appeller getCost pour que le coût soit mis à jour.
                tournee.getCost();
                //System.out.println(tournee);

                // Enfin, on l'ajoute à la liste.
                tournees.add(tournee);
            }
            
            System.out.println("Temps écoulé : " + ((System.currentTimeMillis() - beginTime)/1000) + "/" + timeoutSeconds + "s");
            System.out.println(tournees.size() + " tournées hypothétiques générées !");
        }
        
        return tournees;
    }
    
    /**
     * Permet de choisir une commandeClient de manière aléatoire dans une liste de clients
     * @param clients La liste des clients parmi lesquels on peut piocher
     * @return Retourne une CommandeClient au hasard
     */
    public static CommandeClient choisirCommandeRandom(List<CommandeClient> clients)
    {
        Random randomGenerator = new Random();
        int randomIndex = randomGenerator.nextInt(clients.size());
        CommandeClient commandeRandom = clients.get(randomIndex);
        return commandeRandom;
    }

    private static Set<HypoTournee> generateSafeTournees(List<CommandeClient> clientsAll, Depot depot) {
        
        Set<HypoTournee> tournees = new HashSet<>();
        
        for(CommandeClient client : clientsAll) {
            HypoTournee tournee;
            
            if(client.getNombreRemorquesMax() < 2) {
                tournee = new HypoTournee(HypoTournee.Type.CAMION);
            } else {
                tournee = new HypoTournee(HypoTournee.Type.TRAIN);
            }
            
            tournee.addLieu(depot);
            tournee.addLieu(client);
            tournee.addLieu(depot);

            // A appeller pour générer le coût
            tournee.getCost();
            tournees.add(tournee);
        }
        
        return tournees;
    }
    
    /**
     * Fonction de test qui permet de tester la fonction choisirCommandeRandom(List<CommandeClient> clients)
     */
    public void testChoisirCommandeRandom()
    {
        List<CommandeClient> clients = new ArrayList<CommandeClient>();
        
        CommandeClient client1 = new CommandeClient();
        client1.setCodePostal("1");
        clients.add(client1);
        
        CommandeClient client2 = new CommandeClient();
        client2.setCodePostal("2");
        clients.add(client2);
        
        CommandeClient client3 = new CommandeClient();
        client3.setCodePostal("3");
        clients.add(client3);
        
        CommandeClient clientRandom = this.choisirCommandeRandom(clients);
        System.out.println(clientRandom.getCodePostal());
    }
    
    public static void main(String[] args) 
    {
        int generateTourneesDuringSeconds = 30;
        int cplexSolveLimitSeconds = generateTourneesDuringSeconds;
        
        System.out.println("STEP 1 / READING FLEET.CSV");
        CSVReader reader = new CSVReader();
        reader.readFleet();
        
        
        System.out.println("STEP 2 / GENERATING TOURNEES");
        Set<HypoTournee> hypoTournees = makeTourneesRandom(generateTourneesDuringSeconds);
        
        System.out.println("STEP 3 / SOLVING CPLEX");
        CplexSolve cp = new CplexSolve();
        for(CplexTournee ct: hypoTournees) {
            cp.addTournee(ct);
        }
        cp.setTimeLimit(cplexSolveLimitSeconds);
        cp.setEmphasis(CplexSolve.MIPEmphasis.OPTIMALITY);
        cp.solve();
        ArrayList<CplexTournee> results = cp.getResults();
        System.out.println("Results found : " + results.size());
        
        System.out.println("STEP 4 / SAVING RESULTS");
        DeCplexifier dec = new DeCplexifier();
        dec.CplexTourneesToSolution(results);
        
        System.out.println("STEP 5 / WRITING SOLUTION.CSV");
        CSVWriter writer = new CSVWriter();
        writer.WriteCSV();
    }
    
    public static void setConstantes() {
        Constantes.capaciteMax = 500;
        Constantes.dureeMaxTournee = 8 * 3600;
        Constantes.coutCamion = 100;
        Constantes.coutSecondeRemorque = 50;
        Constantes.coutDureeCamion = 20;
        Constantes.coutTrajetCamion = (float) 0.5;
        Constantes.coutTrajetSecondeRemorque = (float) 0.2;
    }
}
