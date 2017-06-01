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
import ordo.data.entities.Colis;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.SwapBody;
import ordo.data.entities.Vehicule;
import ordo.data.entities.Depot;
import ordo.data.entities.VehiculeAction;

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
            createFakeConsts();
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
        try{
            CommandeClient cc_test = daoCommandeClient.findAll().iterator().next();
        }
        catch(Exception e){
            System.out.println("Il n'y a aucune commandes client en base créations de fausses commande");
            createFakeCmds();
        }
            
        // On get le depot
        Depot dp;
        
        try{
            dp = daoDepot.findAll().iterator().next();
        }
        catch(Exception e){
            System.out.println("Il n'y a encore aucun depot nous créeons donc un dépot avec des coordonnées aléatoires");
            //Qui en fait ne sont pas si aléatoire
            dp = new Depot();
            dp.setCoordX((float)8.42227);
            dp.setCoordY((float)49.45044);
            dp.setCodePostal("67069");
            dp.setNumeroLieu("D1");
            dp.setVille("Lens");
            daoDepot.create(dp);
        }
            
        // on get tous les clients et leurs demandes
        Collection<CommandeClient> ccc = daoCommandeClient.findAll();
        
        // On crée une liste de véhicule pour les clients
        List<Vehicule> lv = new ArrayList();
        
        // On crée une liste de véhicule Action pour les clients
        List<VehiculeAction> lva = new ArrayList();
        
        // On crée un Véhicule vide qui nous servira de point se stockage pour les clients
        Vehicule tmp_v;
        
        // On crée un Véhicule Action vide qui nous servira de point se stockage pour les clients
        VehiculeAction tmp_va;
        
        // On crée un colis vide qui nous servira de point de stockage 
        Colis tmp_c;
        
        //On boucle sur les commandes clients
        for (Iterator<CommandeClient> iter = ccc.iterator(); iter.hasNext(); ) {
            CommandeClient cc = iter.next();
            tmp_v = new Vehicule();
            if(cc.getQuantiteVoulue() > Constantes.capaciteMax){
                tmp_c = new Colis();
                
                tmp_c.setCommande(cc);
                tmp_c.setQuantite(Constantes.capaciteMax);
                tmp_v.getSwapBodies().get(0).addColis(tmp_c); // On remplis le premier camion à bloc
                
                
                // On fait un nouveau colis avec le restant de la commande
                tmp_c = new Colis();
                tmp_c.setCommande(cc);
                tmp_c.setQuantite(cc.getQuantiteVoulue() - Constantes.capaciteMax);
                tmp_v.addSwapBody(new SwapBody());
                tmp_v.getSwapBodies().get(1).addColis(tmp_c);
                tmp_v.add(cc);
            }
            else{
                tmp_c = new Colis();
                tmp_c.setCommande(cc);
                tmp_c.setQuantite(cc.getQuantiteVoulue());
                tmp_v.add(cc);
                tmp_v.getSwapBodies().get(0).addColis(tmp_c);
            }
            daoVehicule.create(tmp_v);
        }
        
        //On effectue ensuite les tournees
        for (Iterator<Vehicule> iter = lv.iterator(); iter.hasNext(); ) {
            Vehicule v = iter.next();
            tmp_va = new VehiculeAction();
            tmp_va.setDepart(dp);
            tmp_va.setArrivee(v.getCommandes().iterator().next());
            v.addAction(tmp_va);
            
            tmp_va = new VehiculeAction();
            tmp_va.setDepart(v.getCommandes().iterator().next());
            tmp_va.setArrivee(dp);
            v.addAction(tmp_va);
            
            // On persiste les véhicules
            System.out.println(v);
            daoVehicule.create(v);
        }
        System.out.println(daoDepot.findAll());
    }
    
    public static void makeSolutionV2(){
        //On test si les constantes sont bien initialisées
        if(!isInitialized()){
            // Si ce n'est pas le cas on affiche le message d'erreur en disant que l'on utilise un jeu de test
            System.out.println("DEV MODE ACTIVATED CREATING FAKE VALUES FOR TESTING");
            // Création des constantes manquantes
            createFakeConsts();
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
        try{
            CommandeClient cc_test = daoCommandeClient.findAll().iterator().next();
        }
        catch(Exception e){
            System.out.println("Il n'y a aucune commandes client en base créations de fausses commande");
            createFakeCmds();
        }
            
        // On get le depot
        Depot dp;
        
        try{
            dp = daoDepot.findAll().iterator().next();
        }
        catch(Exception e){
            System.out.println("Il n'y a encore aucun depot nous créeons donc un dépot avec des coordonnées aléatoires");
            //Qui en fait ne sont pas si aléatoire
            dp = new Depot();
            dp.setCoordX((float)8.42227);
            dp.setCoordY((float)49.45044);
            dp.setCodePostal("67069");
            dp.setNumeroLieu("D1");
            dp.setVille("Lens");
            daoDepot.create(dp);
        }
        
        
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
    
    private static void createFakeConsts(){
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
    
    private static void createFakeCmds(){
        //on get la dao
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        
        CommandeClient cc = new CommandeClient();
        cc.setCodePostal("75015");
        cc.setCoordX((float)8.68674);
        cc.setCoordY((float)49.03529);
        cc.setDureeService(3000);
        cc.setLibelle("C1");
        cc.setNombreRemorquesMax(1);
        cc.setQuantiteVoulue(400);
        cc.setVille("Bretten-Rinklingen");
        daoCommandeClient.create(cc);
        
        cc = new CommandeClient();
        cc.setCodePostal("68199");
        cc.setCoordX((float)8.51206);
        cc.setCoordY((float)49.43919);
        cc.setDureeService(2640);
        cc.setLibelle("C2");
        cc.setNombreRemorquesMax(1);
        cc.setQuantiteVoulue(480);
        cc.setVille("Mannheim");
        daoCommandeClient.create(cc);
        
        cc = new CommandeClient();
        cc.setCodePostal("55606");
        cc.setCoordX((float)7.44494);
        cc.setCoordY((float)49.79178);
        cc.setDureeService(1980);
        cc.setLibelle("C3");
        cc.setNombreRemorquesMax(1);
        cc.setQuantiteVoulue(250);
        cc.setVille("Kirn");
        daoCommandeClient.create(cc);
    }
    
    public static void main(String[] args) {
        makeSolutionV1();
        //testCascade();
    }
}
