/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.List;
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
    
    public static void makeTourneesRandom()
    {
        boolean OK1 = true;
        boolean OK2 = false;
        List<CommandeClient> clientsTrains = new ArrayList<>();
        List<CommandeClient> clientsCamions = new ArrayList<>();
        
        
        // Dans un premier temps on a besoin de récupérer les instances de chaques objets
        JpaVehiculeDao          daoVehicule         = JpaVehiculeDao.getInstance();
        JpaVehiculeActionDao    daoVehiculeAction   = JpaVehiculeActionDao.getInstance();
        JpaLieuDao              daoLieu             = JpaLieuDao.getInstance();
        JpaDepotDao             daoDepot            = JpaDepotDao.getInstance();
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        JpaSolutionDao          daoSolution         = JpaSolutionDao.getInstance();
        JpaSwapBodyDao          daoSwapBodyDao      = JpaSwapBodyDao.getInstance();
        JpaTrajetDao            daoTrajet           = JpaTrajetDao.getInstance();
        
        List<Depot> depots = (List<Depot>) daoDepot.findAll();
        Depot depot = depots.get(0);
        clientsTrains = daoCommandeClient.findAllTrains();
        clientsCamions = daoCommandeClient.findAllCamions();
        
        
        for(CommandeClient client : clientsTrains)
        {
            OK1 = true;
            OK2 = false;
            
            HypoTournee tournee = new HypoTournee();
            tournee.addLieu(depot);
            tournee.addLieu(client);
            
            Trajet trajet = daoTrajet.find(depot, client);
            
            tournee.setDuree(tournee.getDuree() + Long.parseLong(trajet.getDuree() + ""));
            
            //Liste des clients restants
            List<Lieu> clientsRestants = new ArrayList<>(clientsTrains);
            clientsRestants.remove(client);
            
            while(OK1 == true)
            {
                //Randomisation = ajout d'un client aléatoire
                CommandeClient nouveauClient = choisirCommandeRandom(clientsRestants);
                //CommandeClient nouveauClient = new CommandeClient();
                
                //On récupère le dernier client avant le random
                CommandeClient dernierClient = (CommandeClient) tournee.getLieux().get(tournee.getLieux().size());
                //On récupère le trajet entre les deux
                Trajet ceTrajet = daoTrajet.find(dernierClient, nouveauClient);
                //On ajoute le nouveau client à la tournée
                tournee.addLieu(nouveauClient);
                //On met à jour la durée
                //On met à jout la distance
                //On met à jour la capacité
                
                
                
                
                clientsRestants.remove(nouveauClient);
            }
            Lieu dernierLieu = tournee.getLieux().get(tournee.getLieux().size());
            tournee.removeLieu(dernierLieu);
            
            //tournee.addLieu(depot);
                                
            //while()
                
            
            
        }
    }
    
    public static void main(String[] args) {
        
    }
}
