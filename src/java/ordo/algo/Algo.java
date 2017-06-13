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
            
            for(CommandeClient cc: v.getCommandes()) {
                cc.setLivree(true);
                daoCommandeClient.update(cc);
            }
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
        
        for(CommandeClient cc: lcc){
            //On prend le client le plus éloigné.
            //On fait ensuite la liste des clients les plus proches de ce dernier
            List<CommandeClient> liste_client_proche = new ArrayList();
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
                if(v.getQuantity() + ccp.getQuantiteVoulue() > Constantes.capaciteMax *2)continue;
                // Si 
            }
        }
        
    }
    
    private static void generateVéhiculeAction(Vehicule v){
        JpaTrajetDao    daoTrajet   = JpaTrajetDao.getInstance();
        Depot dp = getDepot();
        // Si on a aucune commandes dans le Véhicule
        if(v.getCommandes().size() == 0) return;
        
        //On effectue une sauvegarde temporaire des VéhiculeAction
        Collection<VehiculeAction> save_va;
        save_va = v.getActions();
        
        //On vide les anciens VehiculeAction
        v.getActions().clear();
        
        VehiculeAction va1;
        VehiculeAction vt;
        
        // <editor-fold defaultstate="collapsed" desc="Description Algo de placage des véhicules">
        
        // @author Nicolas Hansse
        
        //On creer une matrice qui à la forme suivante qui sera un Radis
        //
        //      |  D1  |  C1  |  C2  |  C3  |
        //  D1  |  00  |  10  |  20  |  15  |
        //  C1  |  10  |  00  |  08  |  12  |
        //  C2  |  20  |  07  |  00  |  10  |
        //  C3  |  15  |  12  |  10  |  00  |
        //
        // On suppose que les lignes sont les départ et les colones les arrivées
        // On lit donc que pour aller de C1 -> C2 cela coute 8
        // Les valeurs dans la matrice correpondent au temps pour aller d'un lieu (row) à un autre (col)
        // Le but de l'algo ici et de trouver le chemin le plus rapide pour deservir tout les clients
        // On pourrait tester tous les chemins mais cela serait une factorielle proportionelle au nombres de clients dans le véhicule
        // Rien qu'ici pour 3 clients et le dépot on arrive à 24 possibilité
        // Et le simple fait de monter à 10 clients dans la commande fait grimper ce nombre à 3 628 800 combinaisons
        
        // Pour éviter que cela n'arrive on va se baser sur cet algo qui est plutôt simple mais efficace
        
        // 1ère étape, on prend chaques ligne de la matrice et on repère la plus petite valeur (OR 0)
        // On obtient donc les relations suivantes
        // pour marquer les esprits on note notre relation la matrice "tomate"
        //
        //  D1 -> C1 (10)
        //  C1 -> C2 (8)
        //  C2 -> C1 (7)
        //  C3 -> C2 (10)
        
        // On pose alors la question, y a t'il, dans la liste des relation Cx -> Cy | Cy -> Cx
        // Si oui, pour chacun des couple on test lequel est le moins cher
        // Ici nous avons :
        
        //  C1 -> C2 (8)
        //  C2 -> C1 (7)
        
        // Donc on garde que le moins cher à savoir la deuxième C2 -> C1 (7)
        // En ce qui concerne la deuxième relation C1 -> C2 (8)
        // On va regarder quel est la deuxième relation la moins cher
        // A savoir C1 -> D1 (10)
        
        //On met donc notre "tomate" à jour
        //
        //  D1 -> C1 (10)
        //  C1 -> D1 (10)
        //  C2 -> C1 (7)
        //  C3 -> C2 (10)
        
        //On recommence l'étape 1, y a t'il, dans la liste des relation Cx -> Cy | Cy -> Cx
        //On constate que non, (on peut le detecter en regardant si on ne demmare pas plusieurs fois du même client)
        
        // On pose alors la question numero 2
        // Es-ce que l'on a des relations Cy -> Cx | Cz -> Cx (plusieurs fois le même client en destination)
        // Oui c'est actuellement notre cas. Alors on fait la démarche suivante
        // Pour chaque relation Cx -> Cy | Cz -> Cx on regarde laquelle et la plus économique
        // On a donc :
        
        //  D1 -> C1 (10)
        //  C2 -> C1 (7)
        
        // On concerve donc C2 -> C1 (7)
        // Pour D1 -> C1 (10) on cherche la liason la moins cher après (10) en partant de D1
        // On tombe sur D1 -> C3 (15)
        
        // Notre tomate ressemble donc à cela maintenant
        //  D1 -> C3 (15)
        //  C1 -> D1 (10)
        //  C2 -> C1 (7)
        //  C3 -> C2 (10)
        
        // On pose donc la question suivante, y a t'il, dans la liste des relation Cx -> Cy | Cy -> Cx ?
        // Non ? alors es-ce que l'on a des relations Cy -> Cx | Cz -> Cx (plusieurs fois le même client en destination)
        // Non ? alors félication nous avons fini l'algo.
        
        // Nous savons donc que la meilleure route est D1 -> C3 -> C2 -> C1 -> D1
        
        // Voilà pour la version "simplifié" de l'algo
        // en effet nous ne tenons pas compte des clients camion ou train ni des swapBodies
        
        // Voici donc l'algo prenant cette fois si les swapBodies
        // On suppose dans cette version que l'on ne prendra que un seul swapLocation pour toute la tournée
        // Une perspective d'évolution serait de pouvoir en utliser plusieurs si l'on peut
        // Pour récuperer notre swapLocation on récupère le plus proche de tout les clients camion présents dans la liste
        // Pour voir le fonctionnement regarder : //TODO METTRE NOM method calcul swap body
        // Une fois que l'on a notre swapLocation, voici comment est modifiée l'algo
        
        // On creer une matrice qui à la forme suivante (la même que la précedente)
        //
        //      |  D1  |  C1  |  C2  |  C3  |  S1  |
        //  D1  |  00  |  10  |  20  |  15  |  15  |
        //  C1  |  10  |  00  |  08  |  12  |  06  |
        //  C2  |  20  |  07  |  00  |  10  |  05  |
        //  C3  |  15  |  12  |  10  |  00  |  06  |
        //  S1  |  15  |  06  |  05  |  06  |  00  |
        
        // Sauf qu'ici nous n'avons pas qu'une seule matrice
        // Nous avons la matrice que nous appelerons carotte
        // Cette matrice à la particularité de fournir 3 informations
        // Si la valeur vaut -1 le client doit enlever une remorque (train -> camion )
        // Si la valeur vaut 0 pas besoin de passer par un swapLocation ( camion -> train || train -> train || camion -> camion)
        // Si la valeur vaut 1 le client doit prendre obligatoirement le swapbody (revenir au depot par exemple, ou commande client > Capacité max)
        
        // On suppose C1 Camion, C2 train, C3 camion
        // donc
        
        //      |  D1  |  C1  |  C2  |  C3  |
        //  D1  |  00  |  -1  |  00  |  -1  |
        //  C1  |  01  |  00  |  01  |  00  |
        //  C2  |  01  |  -1  |  00  |  00  |
        //  C3  |  01  |  -1  |  01  |  00  |
        
        // On prend également en compte la quantité a livrer pour chaque clients
        
        //      |  C1  |  C2  |  C3  |
        //   Q  | 0100 | 0400 | 0100 |
        
        // En pose pour l'exemple la régle suivante:
        // Capacité max : 300
        
        // Nous avons maintenant toutes les informations pour pouvoir faire nos routes
        // Comme avant on réalise notre "tomate"
        // On prend les clients et les dépots au niveau des lignes et on cherche le moins cher
        // Sauf qu'ici par moins cher on prend en compte notre "carotte"
        // Donc quand on regarde une relation Cx -> Cy dans la matrice tomate
        // On regarde si la valeur de la relation Cx -> Cy dans la matrice carotte est dif de 0
        // Quand cette valeur est différente de 1 cela veut dire que l'on a forcément besoin de passer au swapLocation (pour park ou pickup)
        // Donc si c'est le cas,
        // Pour une relation Cx -> Cy demandant un passage par un swapLocation
        // Son cout sera donc de Cx -> Sx + Sx -> Cy
        
        // On change donc quelques données dans l'algo de départ
        //      |  D1  |  C1  |  C2  |  C3  |  S1  |
        //  D1  |  00  |  10  |  20  |  15  |  15  |
        //  C1  |  10  |  00  |  08  |  13  |  06  |
        //  C2  |  20  |  07  |  00  |  10  |  06  |
        //  C3  |  15  |  12  |  10  |  00  |  06  |
        //  S1  |  15  |  06  |  05  |  06  |  00  |
        
        //On obtient donc la tomate suivante en tennant compte de la carrote
        
        //  D1 -> C2 (20)
        //  C1 -> C2 (11)
        //  C2 -> C1 (12)
        //  C3 -> C1 (12)
        
        // On pose alors la question, y a t'il, dans la liste des relation Cx -> Cy | Cy -> Cx
        // Si oui, pour chacun des couple on test lequel est le moins cher
        // Ici nous avons :
        
        //  C1 -> C2 (11)
        //  C2 -> C1 (12)
        
        // Donc on garde que le moins cher à savoir la deuxième C1 -> C2 (11)
        // En ce qui concerne la deuxième relation C2 -> C1 (12)
        // On va regarder quel est la deuxième relation la moins cher
        // A savoir C2 -> C3 (12)
        
        // On a donc la matrice suivante
        
        //  D1 -> C2 (20)
        //  C1 -> C2 (11)
        //  C2 -> C3 (12)
        //  C3 -> C1 (12)
        
        //On recommence l'étape 1, y a t'il, dans la liste des relation Cx -> Cy | Cy -> Cx
        //On constate que non, (on peut le detecter en regardant si on ne demmare pas plusieurs fois du même client)
        
        // On pose alors la question numero 2
        // Es-ce que l'on a des relations Cy -> Cx | Cz -> Cx (plusieurs fois le même client en destination)
        // Oui c'est actuellement notre cas. Alors on fait la démarche suivante
        // Pour chacune de ces relations, existe t'il une relation dont le départ n'est dans aucune arrivée de l'ensemble des relations du système.
        //      Ce qui est le cas ici, D1 n'apparait jamais en arrivé.
        //      Donc si c'est le cas, on retire notre relation de la liste.
        // Sinon
        // Pour chaque relation Cx -> Cy | Cz -> Cx on regarde laquelle et la plus économique
        // On a donc :
        
        //  D1 -> C2 (20)
        //  C1 -> C2 (11)
        
        // sauf que D1 -> C2 (20) est supprimé
        
        // il nous reste alors que 
        
        // C1 -> C2 (11)
        // Le client ayant le plus bas cout après C2 est :
        
        // C1 -> C3 (13)
        
        // On se retrouve donc avec la tomate suivante
        
        
        //  D1 -> C2 (20)
        //  C1 -> C3 (13)
        //  C2 -> C3 (12)
        //  C3 -> C1 (12)
        
        //On recommence l'étape 1, y a t'il, dans la liste des relation Cx -> Cy | Cy -> Cx
        //On constate que non, (on peut le detecter en regardant si on ne demmare pas plusieurs fois du même client)
        
        // On repose alors la question numero 2
        // Es-ce que l'on a des relations Cy -> Cx | Cz -> Cx (plusieurs fois le même client en destination)
        // Oui c'est actuellement notre cas. Alors on fait la démarche suivante
        // Pour chacune de ces relations, existe t'il une relation dont le départ n'est dans aucune arrivée de l'ensemble des relations du système.
        //      Ce qui n'est pas le cas ici, C1 et C2 apparaissent en arrivé dans d'autres relations.
        //      Donc si c'est le cas, on retire notre relation de la liste.
        // Sinon
        // Pour chaque relation Cx -> Cy | Cz -> Cx on regarde laquelle et la plus économique
        // On a donc :
        
        //  C1 -> C3 (13)
        //  C2 -> C3 (12)
        
        // On garde donc le moins cher des 2 
        // On garde donc C2 -> C3 (12)
        
        // On regarde le client suivant pour
        
        //  C1 -> C3 (13)
        // deviens
        //  C1 -> D1 (21)
        
        //On obtient donc la matrice tomate suivante
        
        //  D1 -> C2 (20)
        //  C1 -> D1 (21)
        //  C2 -> C3 (12)
        //  C3 -> C1 (12)
        
        //Et ici elle prend bien en compte les swapLocations
        
        // </editor-fold>
        
        
        
    }
    
    private static void generateColis(Vehicule v, CommandeClient cc){
        Colis c; // On creer un objet Colis
        //On test si on peut mettre la commande dans le premier swap body
        if(cc.getQuantiteVoulue() > Constantes.capaciteMax){
            c = new Colis();
            c.setCommande(cc);
            c.setQuantite(Constantes.capaciteMax);
            v.getSwapBodies().get(0).addColis(c); // On remplis le premier camion à bloc


            // On fait un nouveau colis avec le restant de la commande
            c = new Colis();
            c.setCommande(cc);
            c.setQuantite(cc.getQuantiteVoulue() - Constantes.capaciteMax);
            v.addSwapBody(new SwapBody());
            v.getSwapBodies().get(1).addColis(c);
        }
        else{
            c = new Colis();
            c.setCommande(cc);
            c.setQuantite(cc.getQuantiteVoulue());
            v.add(cc);
            v.getSwapBodies().get(0).addColis(c);
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
