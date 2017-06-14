/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    
    public static List<HypoTournee> makeTourneesCamionsRandom()
    {
        boolean OK1 = true;
        boolean OK2 = false;
        List<CommandeClient> clientsTrains = new ArrayList<>();
        List<CommandeClient> clientsCamions = new ArrayList<>();
        
        Constantes.capaciteMax = 500;
        Constantes.dureeMaxTournee = 28800;
        //Constantes.dureeMaxTournee = 28800;
        Constantes.coutCamion = 100;
        Constantes.coutDureeCamion = 20;
        Constantes.coutSecondeRemorque = 50;
        Constantes.coutTrajetCamion = (float) 0.5;
        Constantes.coutTrajetSecondeRemorque = (float) 0.2;
        
        // Dans un premier temps on a besoin de récupérer les instances de chaques objets
        JpaDepotDao             daoDepot            = JpaDepotDao.getInstance();
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        JpaTrajetDao            daoTrajet           = JpaTrajetDao.getInstance();
        
        List<Depot> depots = (List<Depot>) daoDepot.findAll();
        Depot depot = depots.get(0);
        clientsTrains = daoCommandeClient.findAllTrains();
        clientsCamions = daoCommandeClient.findAllCamions();
        List<HypoTournee> hypoTourneesCamions = new ArrayList<HypoTournee>();
        
        for(CommandeClient client : clientsCamions)
        {
            try {
                System.out.println("Nouvelle tournée !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                OK1 = true;
                OK2 = false;

                HypoTournee tournee = new HypoTournee();
                tournee.addLieu(depot);
                tournee.addLieu(client);

                System.out.println("On a ajouté le dépot et la première tournée");
                testCodePostal(tournee);

                Trajet trajet = daoTrajet.find(depot, client);

                tournee.setDuree(trajet.getDuree());
                tournee.setDistance(trajet.getDistance());
                tournee.setQuantite(client.getQuantiteVoulue());

                //Liste des clients restants
                List<CommandeClient> clientsRestants = new ArrayList<>(clientsTrains);
                clientsRestants.remove(client);

                while(OK1 == true)
                {
                    //Randomisation = ajout d'un client aléatoire
                    CommandeClient nouveauClient = choisirCommandeRandom(clientsRestants);

                    //On récupère le dernier client avant le random
                    CommandeClient dernierClient = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);

                    //On récupère le trajet entre les deux
                    Trajet ceTrajet = daoTrajet.find(dernierClient, nouveauClient);

                    //On ajoute le nouveau client à la tournée
                    tournee.addLieu(nouveauClient);

                    System.out.println("Première boucle : On a ajouté un nouveau client à la tournée");
                    testCodePostal(tournee);

                    //On met à jour la durée
                    tournee.setDuree(tournee.getDuree() + ceTrajet.getDuree());

                    //On met à jout la distance
                    tournee.setDistance(tournee.getDistance() + ceTrajet.getDistance());

                    //On met à jour la capacité
                    tournee.setQuantite(tournee.getQuantite() + nouveauClient.getQuantiteVoulue());

                    //On retire ce client des possibilités pour le prochain tirage
                    clientsRestants.remove(nouveauClient);

                    //On teste si la tournée est toujours correcte
                    if(tournee.isTooFull() || tournee.isTooLong())
                    {
                        OK1 = false;
                    }
                }
                //Si on sort de la boucle, c'est qu'un lieu est en trop : on le retire
                CommandeClient dernierLieu = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);
                tournee.removeLieu(dernierLieu);
                System.out.println("On retire le client de trop de la tournée");
                testCodePostal(tournee);

                //Mise à jour de distance, temps, quantité après retrait du dernier client
                CommandeClient lieuEncoreAvant = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);
                Trajet ceTrajet = daoTrajet.find(lieuEncoreAvant, dernierLieu);
                tournee.setDistance(tournee.getDistance() - ceTrajet.getDistance());
                tournee.setDuree(tournee.getDuree() - ceTrajet.getDuree());
                tournee.setQuantite(tournee.getQuantite() - dernierLieu.getQuantiteVoulue());

                //On ajoute le dépôt pour finir la tournée
                tournee.addLieu(depot);
                System.out.println("On ajoute le dépôt pour finir la tournée");
                testCodePostal(tournee);


                //Update distance, temps
                ceTrajet = daoTrajet.find(lieuEncoreAvant, depot);
                tournee.setDistance(tournee.getDistance() + ceTrajet.getDistance());
                tournee.setDuree(tournee.getDuree() + ceTrajet.getDuree());

                if(tournee.isTooFull() || tournee.isTooLong())
                {
                    OK2 = false;
                }
                else
                {
                    OK2 = true;
                }
                System.out.println(tournee.isTooFull());
                System.out.println(tournee.isTooLong());
                System.out.println(tournee.getDuree());

                while(OK2 == false)
                {    
                    //On retire le depôt
                    Depot dernierDepot = (Depot) tournee.getLieux().get(tournee.getLieux().size() - 1);
                    tournee.getLieux().remove(tournee.getLieux().size() - 1);
                    System.out.println("Deuxième boucle : On retire le dépôt pour retirer le dernier client");
                    testCodePostal(tournee);

                    //Mise à jour distance et temps
                    CommandeClient derniereCommandeClient = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);
                    ceTrajet = daoTrajet.find(derniereCommandeClient, dernierDepot);
                    tournee.setDistance(tournee.getDistance() - ceTrajet.getDistance());
                    tournee.setDuree(tournee.getDuree() - ceTrajet.getDuree());

                    //On retire le dernier lieu ajouté
                    tournee.removeLieu(derniereCommandeClient);
                    System.out.println("Deuxième boucle : On retire le dernier client");
                    testCodePostal(tournee);
                    CommandeClient clientEncoreAvant = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);

                    //Mise à jour distance, temps, quantité
                    ceTrajet = daoTrajet.find(clientEncoreAvant, derniereCommandeClient);
                    tournee.setDistance(tournee.getDistance() - ceTrajet.getDistance());
                    tournee.setDuree(tournee.getDuree() - ceTrajet.getDuree());
                    tournee.setQuantite(tournee.getQuantite() - derniereCommandeClient.getQuantiteVoulue());

                    //On ajoute le dépôt
                    tournee.addLieu(depot);  
                    System.out.println("Deuxième boucle : On rajoute le dépôt pour finir la tournée");
                    testCodePostal(tournee);

                    //Mise à jour distance, temps
                    ceTrajet = daoTrajet.find(clientEncoreAvant, depot);
                    tournee.setDistance(tournee.getDistance() + ceTrajet.getDistance());
                    tournee.setDuree(tournee.getDuree() + ceTrajet.getDuree());


                    if(tournee.isTooFull() || tournee.isTooLong())
                    {
                        OK2 = false;
                    }
                    else
                    {
                        OK2 = true;
                    }
                }

                hypoTourneesCamions.add(tournee);
            } catch (Exception e) {
            }
        }
        
        System.out.println(hypoTourneesCamions.size() + " tournées hypothétiques générées !");
        
        float coutMin = 999999999;
        for(HypoTournee hypotournee: hypoTourneesCamions)
        {
            hypotournee.setCost(hypotournee.getCamionCost());
            System.out.println(hypotournee.getCost());
            if(hypotournee.getCost() < coutMin)
            {
                coutMin = hypotournee.getCost();
            }
        }
        
        System.out.println(coutMin);
        
        return hypoTourneesCamions;
    }
    
    public static List<HypoTournee> makeTourneesTrainsRandom()
    {
        boolean OK1 = true;
        boolean OK2 = false;
        List<CommandeClient> clientsTrains = new ArrayList<>();
        List<CommandeClient> clientsCamions = new ArrayList<>();
        
        Constantes.capaciteMax = 1000;
        Constantes.dureeMaxTournee = 99999;
        //Constantes.dureeMaxTournee = 28800;
        Constantes.coutCamion = 100;
        Constantes.coutDureeCamion = 20;
        Constantes.coutSecondeRemorque = 50;
        Constantes.coutTrajetCamion = (float) 0.5;
        Constantes.coutTrajetSecondeRemorque = (float) 0.2;
        
        // Dans un premier temps on a besoin de récupérer les instances de chaques objets
        JpaDepotDao             daoDepot            = JpaDepotDao.getInstance();
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        JpaTrajetDao            daoTrajet           = JpaTrajetDao.getInstance();
        
        List<Depot> depots = (List<Depot>) daoDepot.findAll();
        Depot depot = depots.get(0);
        clientsTrains = daoCommandeClient.findAllTrains();
        clientsCamions = daoCommandeClient.findAllCamions();
        List<HypoTournee> hypoTourneesTrains = new ArrayList<HypoTournee>();
        
        for(CommandeClient client : clientsTrains)
        {
            try {
                System.out.println("Nouvelle tournée !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                OK1 = true;
                OK2 = false;

                HypoTournee tournee = new HypoTournee();
                tournee.addLieu(depot);
                tournee.addLieu(client);

                System.out.println("On a ajouté le dépot et la première tournée");
                testCodePostal(tournee);

                Trajet trajet = daoTrajet.find(depot, client);

                tournee.setDuree(trajet.getDuree());
                tournee.setDistance(trajet.getDistance());
                tournee.setQuantite(client.getQuantiteVoulue());

                //Liste des clients restants
                List<CommandeClient> clientsRestants = new ArrayList<>(clientsTrains);
                clientsRestants.remove(client);

                while(OK1 == true)
                {
                    //Randomisation = ajout d'un client aléatoire
                    CommandeClient nouveauClient = choisirCommandeRandom(clientsRestants);

                    //On récupère le dernier client avant le random
                    CommandeClient dernierClient = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);

                    //On récupère le trajet entre les deux
                    Trajet ceTrajet = daoTrajet.find(dernierClient, nouveauClient);

                    //On ajoute le nouveau client à la tournée
                    tournee.addLieu(nouveauClient);

                    System.out.println("Première boucle : On a ajouté un nouveau client à la tournée");
                    testCodePostal(tournee);

                    //On met à jour la durée
                    tournee.setDuree(tournee.getDuree() + ceTrajet.getDuree());

                    //On met à jout la distance
                    tournee.setDistance(tournee.getDistance() + ceTrajet.getDistance());

                    //On met à jour la capacité
                    tournee.setQuantite(tournee.getQuantite() + nouveauClient.getQuantiteVoulue());

                    //On retire ce client des possibilités pour le prochain tirage
                    clientsRestants.remove(nouveauClient);

                    //On teste si la tournée est toujours correcte
                    if(tournee.isTooFull() || tournee.isTooLong())
                    {
                        OK1 = false;
                    }
                }
                //Si on sort de la boucle, c'est qu'un lieu est en trop : on le retire
                CommandeClient dernierLieu = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);
                tournee.removeLieu(dernierLieu);
                System.out.println("On retire le client de trop de la tournée");
                testCodePostal(tournee);

                //Mise à jour de distance, temps, quantité après retrait du dernier client
                CommandeClient lieuEncoreAvant = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);
                Trajet ceTrajet = daoTrajet.find(lieuEncoreAvant, dernierLieu);
                tournee.setDistance(tournee.getDistance() - ceTrajet.getDistance());
                tournee.setDuree(tournee.getDuree() - ceTrajet.getDuree());
                tournee.setQuantite(tournee.getQuantite() - dernierLieu.getQuantiteVoulue());

                //On ajoute le dépôt pour finir la tournée
                tournee.addLieu(depot);
                System.out.println("On ajoute le dépôt pour finir la tournée");
                testCodePostal(tournee);


                //Update distance, temps
                ceTrajet = daoTrajet.find(lieuEncoreAvant, depot);
                tournee.setDistance(tournee.getDistance() + ceTrajet.getDistance());
                tournee.setDuree(tournee.getDuree() + ceTrajet.getDuree());

                if(tournee.isTooFull() || tournee.isTooLong())
                {
                    OK2 = false;
                }
                else
                {
                    OK2 = true;
                }
                System.out.println(tournee.isTooFull());
                System.out.println(tournee.isTooLong());
                System.out.println(tournee.getDuree());

                while(OK2 == false)
                {    
                    //On retire le depôt
                    Depot dernierDepot = (Depot) tournee.getLieux().get(tournee.getLieux().size() - 1);
                    tournee.getLieux().remove(tournee.getLieux().size() - 1);
                    System.out.println("Deuxième boucle : On retire le dépôt pour retirer le dernier client");
                    testCodePostal(tournee);

                    //Mise à jour distance et temps
                    CommandeClient derniereCommandeClient = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);
                    ceTrajet = daoTrajet.find(derniereCommandeClient, dernierDepot);
                    tournee.setDistance(tournee.getDistance() - ceTrajet.getDistance());
                    tournee.setDuree(tournee.getDuree() - ceTrajet.getDuree());

                    //On retire le dernier lieu ajouté
                    tournee.removeLieu(derniereCommandeClient);
                    System.out.println("Deuxième boucle : On retire le dernier client");
                    testCodePostal(tournee);
                    CommandeClient clientEncoreAvant = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size() - 1);

                    //Mise à jour distance, temps, quantité
                    ceTrajet = daoTrajet.find(clientEncoreAvant, derniereCommandeClient);
                    tournee.setDistance(tournee.getDistance() - ceTrajet.getDistance());
                    tournee.setDuree(tournee.getDuree() - ceTrajet.getDuree());
                    tournee.setQuantite(tournee.getQuantite() - derniereCommandeClient.getQuantiteVoulue());

                    //On ajoute le dépôt
                    tournee.addLieu(depot);  
                    System.out.println("Deuxième boucle : On rajoute le dépôt pour finir la tournée");
                    testCodePostal(tournee);

                    //Mise à jour distance, temps
                    ceTrajet = daoTrajet.find(clientEncoreAvant, depot);
                    tournee.setDistance(tournee.getDistance() + ceTrajet.getDistance());
                    tournee.setDuree(tournee.getDuree() + ceTrajet.getDuree());


                    if(tournee.isTooFull() || tournee.isTooLong())
                    {
                        OK2 = false;
                    }
                    else
                    {
                        OK2 = true;
                    }
                }

                hypoTourneesTrains.add(tournee);
            } catch (Exception e) {
            }                
        }
        
        System.out.println(hypoTourneesTrains.size() + " tournées hypothétiques générées !");
        
        float coutMin = 999999999;
        for(HypoTournee hypotournee: hypoTourneesTrains)
        {
            hypotournee.setCost(hypotournee.getTrainCost());
            System.out.println(hypotournee.getCost());
            if(hypotournee.getCost() < coutMin)
            {
                coutMin = hypotournee.getCost();
            }
        }
        
        System.out.println(coutMin);
        
        return hypoTourneesTrains;
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
        //AlgoRandom algoRandom = new AlgoRandom();
        //algoRandom.testChoisirCommandeRandom();
        
        AlgoRandom algoRandom = new AlgoRandom();
        for(int j=0; j < 100; j++)
        {
            algoRandom.makeTourneesCamionsRandom();
        }
    }
}
