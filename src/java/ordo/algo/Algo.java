/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaDepotDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSolutionDao;
import ordo.data.dao.jpa.JpaSwapBodyDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.dao.jpa.JpaVehiculeActionDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.Colis;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.SwapBody;
import ordo.data.entities.Vehicule;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.Trajet;
import ordo.data.entities.VehiculeAction;
import ordo.data.metier.CSVReader;

/**
 *
 * @author Nicolas Hansse
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
        try{
            Depot dp = daoDepot.findAll().iterator().next();
        }
        catch(Exception e){
            System.out.println("Il n'y a encore aucun depot nous créeons donc un dépot avec des coordonnées aléatoires");
            //Qui en fait ne sont pas si aléatoire
            Depot dp = new Depot();
            dp.setCoordX((float)8.42227);
            dp.setCoordY((float)49.45044);
            dp.setCodePostal("67069");
            dp.setNumeroLieu("D1");
            dp.setVille("Lens");
            daoDepot.create(dp);
        }
        
        Depot dp = daoDepot.findAll().iterator().next();
            
        // on get tous les clients et leurs demandes
        Collection<CommandeClient> ccc = daoCommandeClient.findAll();
        
        // On crée une liste de véhicule pour les clients
        List<Vehicule> lv = new ArrayList();
        
        // On crée une liste de véhicule Action pour les clients
        List<VehiculeAction> lva = new ArrayList();
        
        
        //On boucle sur les commandes clients
        for (Iterator<CommandeClient> iter = ccc.iterator(); iter.hasNext(); ) {
            CommandeClient cc = iter.next();
            
            // On crée un Véhicule vide qui nous servira de point se stockage pour les clients
            Vehicule tmp_v = new Vehicule();
            
            // On crée deux colis vide qui nous servira de point de stockage 
            Colis tmp_c = new Colis();
            Colis tmp_c2 = new Colis();
            
            if(cc.getQuantiteVoulue() > Constantes.capaciteMax){
                
                tmp_c.setCommande(cc);
                tmp_c.setQuantite(Constantes.capaciteMax);
                tmp_v.getSwapBodies().get(0).addColis(tmp_c); // On remplis le premier camion à bloc
                
                
                // On fait un nouveau colis avec le restant de la commande
                tmp_c2.setCommande(cc);
                tmp_c2.setQuantite(cc.getQuantiteVoulue() - Constantes.capaciteMax);
                tmp_v.addSwapBody(new SwapBody());
                tmp_v.getSwapBodies().get(1).addColis(tmp_c2);
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
            
            lv.add(tmp_v);
        }
        
        //On effectue ensuite les tournees
        for (Iterator<Vehicule> iter = lv.iterator(); iter.hasNext(); ) {
            Vehicule v = iter.next();
            
            CommandeClient client = v.getCommandes().get(0);
            
            VehiculeAction va1 = new VehiculeAction();
            va1.setDepart(dp);
            va1.setArrivee(client);
            va1.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
            va1.setDistance(10000);
            va1.setDuree(6);
            
            Trajet t1 = daoTrajet.find(dp, client);
            if(t1 != null) {
                va1.setDistance(t1.getDistance());
                va1.setDuree(t1.getDuree());
            }
            
            if(v.isTrain()) {
                va1.setIsTrain(true);
            }
            
            v.addAction(va1);
            
            VehiculeAction vt = new VehiculeAction();
            vt.setDepart(client);
            vt.setArrivee(client);
            vt.setEnumAction(VehiculeAction.EnumAction.TRAITEMENT);
            vt.setDistance(0);
            vt.setDuree(client.getDureeService());
            
            if(v.isTrain()) {
                vt.setIsTrain(true);
            }
            
            v.addAction(vt);
            
            VehiculeAction va2 = new VehiculeAction();
            va2.setDepart(client);
            va2.setArrivee(dp);
            va2.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
            va2.setDistance(10000);
            va2.setDuree(6);
            
            Trajet t2 = daoTrajet.find(client, dp);
            if(t2 != null) {
                va2.setDistance(t2.getDistance());
                va2.setDuree(t2.getDuree());
            }
            
            if(v.isTrain()) {
                va2.setIsTrain(true);
            }
            
            v.addAction(va2);
            
            // On persiste les véhicules
            //System.out.println(v);
            daoVehicule.create(v);
        }
        
        for(CommandeClient cc: daoCommandeClient.findAll()) {
            cc.setLivree(true);
            daoCommandeClient.update(cc);
        }
        
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
        final Depot dp = getDepot();
        
        Collection<CommandeClient> ccc = daoCommandeClient.findAll();
        
        for (Iterator<CommandeClient> iter = ccc.iterator(); iter.hasNext(); ) {
            CommandeClient cc = iter.next();
        }
        
        List<CommandeClient> lcc = new ArrayList(ccc);
        
        //On trie la liste du client le plus éloignée 
        Collections.sort(lcc, new Comparator<CommandeClient>(){
            @Override
            public int compare(CommandeClient o1, CommandeClient o2) {
                return (int) (dp.distanceToGoTo(o2) - dp.distanceToGoTo(o1));
            }
        });
        
        List<CommandeClient> lcc_ghost = new ArrayList(lcc);
        
        
        for(Iterator<CommandeClient> iter = lcc.iterator(); iter.hasNext(); ){
            CommandeClient cc = iter.next();
            if(!lcc_ghost.contains(cc)) continue;
            //On prend le client le plus éloigné.
            //On fait ensuite la liste des clients les plus proches de ce dernier
            List<CommandeClient> liste_client_proche = new ArrayList(lcc_ghost);
            liste_client_proche.remove(cc);
            Collections.sort(liste_client_proche, new Comparator<CommandeClient>(){
                @Override
                public int compare(CommandeClient o1, CommandeClient o2) {
                    return (int) (cc.distanceToGoTo(o1) - dp.distanceToGoTo(o2));
                }
            });
            
            
            Vehicule v = new Vehicule(); // On créer un véhicule
            v.add(cc); // On y met la commande cliente
            
            //Pour chaque autre client proche de se dernier on test si leurs commandes peuvent être mises dans le véhicule
            for(CommandeClient ccp : liste_client_proche){
                // Si la commande dépasse ce que peut supporter le véhicule on passe à la commande suivante
                if(v.getClientQuantity() + ccp.getQuantiteVoulue() > Constantes.capaciteMax *2)continue;
                if(v.getCommandes().size() > 2)break;
                else{
                    v.add(ccp);
                }
            }
            
            //
            List<VehiculeAction> lva = new Radis(dp, v.getCommandes()).wololo().generateVehiculeAction();
            
            while(getTempsTournee(lva) > Constantes.dureeMaxTournee){
                v.getCommandes().remove(v.getCommandes().size() - 1);
                lva = new Radis(dp, v.getCommandes()).wololo().generateVehiculeAction();
            }
            
            for(VehiculeAction va : lva){
                v.addAction(va);
                
                try{
                    lcc_ghost.remove(va.getArrivee());
                }
                catch(Exception e){
                    
                }
            }
            
            generateColis(v);
            
            daoVehicule.create(v);
            
            for(CommandeClient ccb: v.getCommandes()) {
                ccb.setLivree(true);
                daoCommandeClient.update(ccb);
            }
            
            // On test si le véhicule respecte le temps
        }
    }
    
    private static float getTempsTournee(List<VehiculeAction> lva){
        float rtn = 0;
        for(VehiculeAction va : lva){
            rtn += va.getDuree();
        }
        return rtn;
    }
    
    private static void generateColis(Vehicule v){
        
        //Si la quantité de produit à mettre dans le camion est > à Q
        //On creer un deuxième swapbody
        if(v.getClientQuantity() > Constantes.capaciteMax && v.getSwapBodies().size() == 1)
            v.addSwapBody(new SwapBody());
        
        for(VehiculeAction va : v.getActions()){
            if(va.getEnumAction().equals(VehiculeAction.EnumAction.DEPLACEMENT) && va.getArrivee() instanceof CommandeClient){
                Colis c; // On creer un objet Colis
                //On test si on peut mettre la commande dans le premier swap body
                if(((CommandeClient)va.getArrivee()).getQuantiteVoulue() > Constantes.capaciteMax){
                    c = new Colis();
                    c.setCommande(((CommandeClient)va.getArrivee()));
                    //On test le nombre de swapBody dans le tracteur
                    c.setQuantite(Constantes.capaciteMax - v.getSwapBodies().get(1).getQuantite());
                    v.getSwapBodies().get(1).addColis(c); // On remplis le deuxième swapBody à fond


                    // On fait un nouveau colis avec le restant de la commande
                    Colis c2 = new Colis();
                    c2.setCommande(((CommandeClient)va.getArrivee()));
                    c2.setQuantite(((CommandeClient)va.getArrivee()).getQuantiteVoulue() - Constantes.capaciteMax - c.getQuantite());
                    v.addSwapBody(new SwapBody());
                    v.getSwapBodies().get(1).addColis(c);
                }
                else{
                    
                    if(v.getSwapBodies().get(0).getQuantite() + v.getClientQuantity() < Constantes.capaciteMax){
                        c = new Colis();
                        c.setCommande(((CommandeClient)va.getArrivee()));
                        c.setQuantite(((CommandeClient)va.getArrivee()).getQuantiteVoulue());
                        v.add(((CommandeClient)va.getArrivee()));
                        v.getSwapBodies().get(0).addColis(c);
                    }
                    else if(v.getSwapBodies().size() > 1 && v.getSwapBodies().get(1).getQuantite() + v.getClientQuantity() < Constantes.capaciteMax){
                        c = new Colis();
                        c.setCommande(((CommandeClient)va.getArrivee()));
                        c.setQuantite(((CommandeClient)va.getArrivee()).getQuantiteVoulue());
                        v.add(((CommandeClient)va.getArrivee()));
                        v.getSwapBodies().get(1).addColis(c);
                    }
                    else{
                        
                    }
                }
            }
        }
    }
    
    public static Depot getDepot(){
        JpaDepotDao daoDepot    = JpaDepotDao.getInstance();
        
        try{
            return daoDepot.findAll().iterator().next();
        }
        catch(Exception e){
            Depot dp;
            System.out.println("Il n'y a encore aucun depot nous créeons donc un dépot avec des coordonnées aléatoires");
            //Qui en fait ne sont pas si aléatoire
            dp = new Depot();
            dp.setCoordX((float)8.42227);
            dp.setCoordY((float)49.45044);
            dp.setCodePostal("67069");
            dp.setNumeroLieu("D1");
            dp.setVille("Lens");
            daoDepot.create(dp);
            return dp;
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
    
    public static void createFakeConsts(){
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
        //makeSolutionV1();
        makeSolutionV2();
        //testCascade();
    }
}
