/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.list;
import java.util.Iterator;
import java.util.List;
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
import ordo.data.entities.Vehicule;

/**
 *
 * @author Olivier
 */
public class Algo {
    
    public static void makeSolutionV1(){
        //*******ALGO DE TRI EN VERSION 1*******/
        //Elle ne cherche aucune optimisation et tout les camions passes par un seul swaplocation
        
        //On test si les constantes sont bien initialisées
        if(!isInitialized()){
            // Si ce n'est pas le cas on affiche le message d'erreur en disant que l'on utilise un jeu de test
            System.out.println("DEV MODE ACTIVATED CREATING FAKE VALUES FOR TESTING");
            // Création des constantes manquantes
            createFakeData();
        }
        
        
        
        // Dans un premier temps on a besoin de récupérer les instances de chaques objets
        JpaVehiculeDao          daoVehicule         = JpaVehiculeDao.getInstance();
        JpaVehiculeActionDao    daoVehiculeAction   = JpaVehiculeActionDao.getInstance();
        JpaLieuDao              daoLieu             = JpaLieuDao.getInstance();
        JpaDepotDao             daoDepot            = JpaDepotDao.getInstance();
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        JpaSolutionDao          daoSolution         = JpaSolutionDao.getInstance();
        JpaSwapBodyDao          daoSwapBodyDao      = JpaSwapBodyDao.getInstance();
        JpaTrajetDao            daoTrajet           = JpaTrajetDao.getInstance();
        
        // On dois lire les csv ici
            // TODO READ CSVS.
            
        // on get tous les clients et leurs demandes
        Collection<CommandeClient> ccc = daoCommandeClient.findAll();
        
        // On crée une liste de véhicule
        List<Vehicule> lv = new ArrayList();
        
        // On crée un Véhicule vide qui nous servira de point se stockage
        Vehicule tmp_v = new Vehicule();
        
        //On boucle sur les commandes clients
        for (Iterator<CommandeClient> iter = ccc.iterator(); iter.hasNext(); ) {
            CommandeClient cc = iter.next();
            if(tmp_v.getQuantity() + cc.getQuantiteVoulue() < Constantes.capaciteMax){
                tmp_v.add(cc);
            }
            else{
                tmp_v = new Vehicule();
                if(tmp_v.getQuantity() + cc.getQuantiteVoulue() < Constantes.capaciteMax){
                    tmp_v.add(cc);
                }
                // On a une commande qui ne peut pas rentrer dans un seul swap body
                // On test donc si la commande peut rentrer dans 2 swap body
                else if(tmp_v.getQuantity() + cc.getQuantiteVoulue() < Constantes.capaciteMax*2){ 
                    tmp_v.add(cc);
                }
            }
        }
        
        // On effectue une solution triviale on va donc dans un premier temps 
    }
    
    private static boolean isInitialized(){
        boolean rtn = true;
        if(Constantes.capaciteMax == -1){
            rtn=false;
            System.out.println("ERROR Constante capaciteMax non initialisée ! (-1)");
        }
        if(Constantes.dureeMaxTournee == -1){
            rtn=false;
            System.out.println("ERROR Constante dureeMaxTournee non initialisée ! (-1)");
        }
        if(Constantes.dureePark == -1){
            rtn=false;
            System.out.println("ERROR Constante dureePark non initialisée ! (-1)");
        }
        if(Constantes.dureePickup == -1){
            rtn=false;
            System.out.println("ERROR Constante dureePickup non initialisée ! (-1)");
        }
        if(Constantes.dureeSwap == -1){
            rtn=false;
            System.out.println("ERROR Constante dureeSwap non initialisée ! (-1)");
        }
        if(Constantes.dureeExchange == -1){
            rtn=false;
            System.out.println("ERROR Constante dureeExchange non initialisée ! (-1)");
        }
        if(Constantes.coutCamion == -1){
            rtn=false;
            System.out.println("ERROR Constante coutCamion non initialisée ! (-1)");
        }
        if(Constantes.coutDureeCamion == -1){
            rtn=false;
            System.out.println("ERROR Constante coutDureeCamion non initialisée ! (-1)");
        }
        if(Constantes.coutTrajetCamion == -1){
            rtn=false;
            System.out.println("ERROR Constante coutTrajetCamion non initialisée ! (-1)");
        }
        if(Constantes.coutSecondeRemorque == -1){
            rtn=false;
            System.out.println("ERROR Constante coutTrajetTracteur non initialisée ! (-1)");
        }
        if(Constantes.coutTrajetSecondeRemorque == -1){
            rtn=false;
            System.out.println("ERROR Constante coutTrajetSecondeRemorque non initialisée ! (-1)");
        }
        return rtn;
    }
    
    private static void createFakeData(){
        if(Constantes.capaciteMax == -1){
            Constantes.capaciteMax = 1000;
        }
        if(Constantes.dureeMaxTournee == -1){
            Constantes.dureeMaxTournee = 39600;
        }
        if(Constantes.dureePark == -1){
            Constantes.dureePark = 300;
        }
        if(Constantes.dureePickup == -1){
            Constantes.dureePickup = 300;
        }
        if(Constantes.dureeSwap == -1){
            Constantes.dureeSwap = 600;
        }
        if(Constantes.dureeExchange == -1){
            Constantes.dureeExchange = 900;
        }
        if(Constantes.coutCamion == -1){
            Constantes.coutCamion = 100;
        }
        if(Constantes.coutDureeCamion == -1){
            Constantes.coutDureeCamion = 20;
        }
        if(Constantes.coutTrajetCamion == -1){
            Constantes.coutTrajetCamion = 1/2 ;
        }
        if(Constantes.coutTrajetSecondeRemorque == -1){
            Constantes.coutTrajetSecondeRemorque = 0;
        }
        if(Constantes.coutSecondeRemorque == -1){
            Constantes.coutSecondeRemorque = 1/5;
        }
    }
}
