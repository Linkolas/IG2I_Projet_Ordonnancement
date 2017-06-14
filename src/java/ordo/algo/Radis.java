/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ordo.algo.Tomate.Chemin;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.SwapBody;
import ordo.data.entities.SwapLocation;
import ordo.data.entities.Vehicule;

// <editor-fold defaultstate="collapsed" desc="Description Algo d'ordre de passage des véhicules">
        
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

/**
 *
 * @author Olivier
 */
public class Radis {
    
    //Attributs
    
    // Liste Clients
    List<CommandeClient> lcc;
    
    // Le SwapLocation
    SwapLocation sl;
    
    // Le Depot
    Depot dp;
    
    // La matrice carrote
    Carotte c = new Carotte();
    
    // La Matrice
    //   ROW         COL     VAL
    Map<String, Map<String, Float>> matrice;

    /**
     * Constructeur par default.
     */
    public Radis() {
        lcc =   new ArrayList();
        sl  =   null;
        dp  =   null;
        c   =   null;
        matrice = null;
    }
    
    /**
     * Constructeur de Radis, celui ci demande un Dépot et une liste de Client
     * On determine automatiquement quel est le swapLocation le plus proche
     * @param dp
     * @param lcc 
     */
    public Radis(Depot dp, List<CommandeClient> lcc){
        if(dp == null)
            throw new UnsupportedOperationException("Depot cannot be null");
        
        this.dp         =   dp;
        this.lcc        =   lcc;
        this.sl         =   getNearestSwapLocation(lcc);
        this.c          =   generateCarotte(lcc, dp);
        this.matrice = new HashMap();
        generateMatrix();
    }
    
    /**
     * Fonction permettant de récuperer le swapLocation le plus proche d'un ensemble de client
     * Pour l'instant on se base sur un point imaginaire qui est la moyenne des distances entre les clients
     * A partir de ce point on cherche le swapLocation le plus proche.
     * 
     * On prend en entree la liste des clients
     * @param lcc
     * 
     * On retourne le swapLocation le proche
     * @return 
     */
    private static SwapLocation getNearestSwapLocation(List<CommandeClient> lcc){
        if(lcc.isEmpty()) return null;
        // On get la swapLocation dao
        JpaSwapLocationDao  daoSwapLocation =   JpaSwapLocationDao.getInstance();
        
        //On doit ensuite faire la moyenne des coord des clients pour calculer le millieu
        float m_x = 0;
        float m_y = 0;
        
        for(CommandeClient cc : lcc){
            m_x += cc.getCoordX();
            m_y += cc.getCoordY();
        }
        
        m_x /= lcc.size();
        m_y /= lcc.size();
        
        SwapLocation boy = null; //boy = bestOneYets
        
        for(SwapLocation sl : daoSwapLocation.findAll()){
            if(boy == null){ boy = sl; continue; }
            if(Math.sqrt(boy.getCoordX()*boy.getCoordX() + boy.getCoordY()*boy.getCoordY()) > Math.sqrt(sl.getCoordX()*sl.getCoordX() + sl.getCoordY()*sl.getCoordY())){
                boy = sl;
            }
        }
        return boy;
        
    }
    
    public Tomate wololo(){
        // La première chose à faire est de creer une tomate à partir des chemins les plus court
        Tomate t = generateInitialTomato();
        System.out.println(t);
        boolean flag = true;
        boolean mainFlag = true;
        while(mainFlag){
            while(flag){
                List<Chemin> chemin_snake = t.containsSnakes();
                for (Iterator<Chemin> iter = chemin_snake.iterator(); iter.hasNext(); ) {
                    Chemin c1 = iter.next();
                    Chemin c2 = iter.next();
                    if(c1.getCost() < c2.getCost()){
                        nextRoad(t, c2);
                    }
                    else{
                        nextRoad(t, c1);
                    }
                }
                if(chemin_snake.isEmpty()){
                    flag = false;
                }
            }
            // On a plus de snakes
            System.out.println(t);
            
            List<Chemin> chemin_twins = t.containsTwins();
            
            Map<Lieu, List<Chemin>> chemin_twins_spec = new HashMap();
            for (Iterator<Chemin> iter = chemin_twins.iterator(); iter.hasNext(); ) {
                Chemin c1 = iter.next();
                if(!chemin_twins_spec.containsKey(c1.getArrivee()))
                    chemin_twins_spec.put(c1.getArrivee(), new ArrayList());
                chemin_twins_spec.get(c1.getArrivee()).add(c1);
            }
            for (Lieu k : chemin_twins_spec.keySet()){
                Chemin boy = null;
                for(Chemin chemin : chemin_twins_spec.get(k)){
                    if(boy == null) { boy = chemin; continue; }
                    if(boy.getCost() > chemin.getCost())
                        boy = chemin;
                }
                //On a recup le meilleur candidat on assigne les autres à d'autres destinations
                System.out.println("FLAG");
                chemin_twins_spec.get(k).remove(boy);
                for(Chemin c :chemin_twins_spec.get(k)){
                    System.out.println(t.toString() + "\n");
                    System.out.println(c);
                    nextRoad(t, c);
                }
            }
            System.out.println(t);
            
            if(t.containsSnakes().isEmpty() && t.containsTwins().isEmpty()){
                mainFlag = false;
            }
        }
        return t;
    }
    
    
    /**
     * Fonction qui prend le trajet le moins cher après lui même
     * Si on un un chemin Cx -> Cy on cherche parmis toutes les autres destinations possibles
     * Quel est la moins cher sachant que Cx -> Cz doit avoir un cout superieur à Cx -> Cy
     * @param t
     * @param c 
     */
    private void nextRoad(Tomate t, Chemin c){
        Chemin chemin;
        String boy = "";
        for(String s_col :this.matrice.get(c.getDepart().getNumeroLieu()).keySet()){
            if(s_col.equals(c.getDepart().getNumeroLieu()) || s_col.equals(c.getArrivee().getNumeroLieu()) || s_col == "S1") continue;
            if(boy == "") { boy = s_col; continue; }
            if(this.matrice.get(c.getDepart().getNumeroLieu()).get(s_col) < this.matrice.get(c.getDepart().getNumeroLieu()).get(boy)
            && this.matrice.get(c.getDepart().getNumeroLieu()).get(s_col) > c.getCost())
                boy = s_col;
        }
        chemin = new Chemin(c.getDepart(), getLieuFromString(boy), this.c.needSwapLocation(c.getDepart(), getLieuFromString(boy)), this.matrice.get(c.getDepart().getNumeroLieu()).get(boy));
        System.out.println("OLD ROAD " + c + " | NEXT ROAD : " + chemin);
        t.replace(c, chemin);
    }
    
    private Tomate generateInitialTomato(){
        Tomate t = new Tomate(sl);
        Chemin chemin; 
        for(String s_row :this.matrice.keySet()){
            if(s_row == "S1") continue;
            String boy = "";
            for(String s_col :this.matrice.get(s_row).keySet()){
                if(s_col == s_row || s_col == "S1") continue;
                if(boy == "") { boy = s_col; continue; }
                
                if(this.matrice.get(s_row).get(s_col) < this.matrice.get(s_row).get(boy))
                    boy = s_col;
            }
            chemin = new Chemin(getLieuFromString(s_row), getLieuFromString(boy), this.c.needSwapLocation(getLieuFromString(s_row), getLieuFromString(boy)), this.matrice.get(s_row).get(boy));
            t.addChemin(chemin);
        }
        
        return t;
    }
    
    public Lieu getLieuFromString(String s_lieu){
        if(s_lieu == "D1") return dp;
        if(s_lieu == "S1") return sl;
        for(CommandeClient cc : this.getLcc()){
            if(cc.getNumeroLieu() == s_lieu)
                return cc;
        }
        return null;
    }
    
    private void generateMatrix(){
        // On ajoute la ligne de dépot
        matrice.put("D1", new HashMap());
        
        // On précise que le chemin D1 -> D1 vaut 0
        matrice.get("D1").put("D1", (float)0);
        
        // On ajoute le swap dans la matrice
        
        // On ajoute la ligne de dépot
        matrice.put("S1", new HashMap());
        
        // On précise que le chemin S1 -> S1 vaut 0
        matrice.get("S1").put("S1", (float)0);
        
        genSwapDepotCol(dp, sl);
        
        // On boucle ensuite sur les clients
        for(CommandeClient cc : this.lcc){
            // On ajoute le client dans la map
            matrice.put(cc.getNumeroLieu(), new HashMap());
            for(CommandeClient cc2 : this.lcc){
                // On ajoute le client dans la map
                if(!c.needSwapLocation(cc, cc2))
                    matrice.get(cc.getNumeroLieu()).put(cc2.getNumeroLieu(), JpaTrajetDao.getInstance().find(cc, cc2).getDistance());
                else{
                    matrice.get(cc.getNumeroLieu()).put(cc2.getNumeroLieu(), JpaTrajetDao.getInstance().find(cc, sl).getDistance() + JpaTrajetDao.getInstance().find(sl, cc2).getDistance());
                }
            }
            
            // On ajoute les liaisons Cx -> D1
            matrice.get(cc.getNumeroLieu()).put("D1", JpaTrajetDao.getInstance().find(cc, dp).getDistance());
            
            // On ajoute les liaisons Cx -> S1
            matrice.get(cc.getNumeroLieu()).put("S1", JpaTrajetDao.getInstance().find(cc, this.sl).getDistance());
            
            
        }
        
    }
    
    /**
     * Ajout manuelle de la ligne de dépot 
     * @param dp 
     */
    private void genSwapDepotCol(Depot dp, SwapLocation sl) {
        
        for(CommandeClient cc_col : this.lcc){
            if(!c.needSwapLocation(dp, cc_col))
                this.matrice.get("D1").put(cc_col.getNumeroLieu(), JpaTrajetDao.getInstance().find(dp, cc_col).getDistance());
            else
                this.matrice.get("D1").put(cc_col.getNumeroLieu(), JpaTrajetDao.getInstance().find(dp, sl).getDistance() + JpaTrajetDao.getInstance().find(sl, cc_col).getDistance());
            
            this.matrice.get("S1").put(cc_col.getNumeroLieu(), JpaTrajetDao.getInstance().find(sl, cc_col).getDistance());
        }
        
        this.matrice.get("D1").put("S1", JpaTrajetDao.getInstance().find(dp, sl).getDistance());
        this.matrice.get("S1").put("D1", JpaTrajetDao.getInstance().find(sl, dp).getDistance());
    }

    public List<CommandeClient> getLcc() {
        return lcc;
    }

    public void setLcc(List<CommandeClient> lcc) {
        this.lcc = lcc;
    }

    public SwapLocation getSl() {
        return sl;
    }

    public void setSl(SwapLocation sl) {
        this.sl = sl;
    }

    public Depot getDp() {
        return dp;
    }

    public void setDp(Depot dp) {
        this.dp = dp;
    }

    public Carotte getC() {
        return c;
    }

    public void setC(Carotte c) {
        this.c = c;
    }

    @Override
    public String toString() {
        String rtn = "\n//// MATRICE RADIS ////\n";
        rtn += "\t  \t";
        for(String d : matrice.keySet()){
            rtn += "|\t" + d + "\t";
        }
        rtn += '\n';
        for(String d : matrice.keySet()){
            rtn += "\t" + d + "\t";
            for(String a: matrice.get(d).keySet()){
                rtn += "|\t" + matrice.get(d).get(a) + "\t";
            }
            rtn += '\n';
        }
        rtn += '\n';
        rtn += '\n';
         
        rtn += this.c.toString();
        return rtn;
    }
    
    
    private static Carotte generateCarotte(List<CommandeClient> lcc, Depot dp){
        return new Carotte(lcc, dp);
    }
    
    public static void main(String[] args) {
        SwapLocation sl = JpaSwapLocationDao.getInstance().find(0);
        
        List<CommandeClient> lcc = new ArrayList();
        
        Vehicule v = new Vehicule();
        v.addSwapBody(new SwapBody());
        
        CommandeClient cc = new CommandeClient();
        cc.setCodePostal("75015");
        cc.setCoordX((float)8.68674);
        cc.setCoordY((float)49.03529);
        cc.setDureeService(3000);
        cc.setLibelle("C1");
        cc.setNumeroLieu("C1");
        cc.setNombreRemorquesMax(1);
        cc.setQuantiteVoulue(400);
        cc.setVille("Bretten-Rinklingen");
        lcc.add(cc);
        v.add(cc);
        
        System.out.println(cc.getNumeroLieu() + " : " + cc.getQuantiteVoulue());
        
        cc = new CommandeClient();
        cc.setCodePostal("68199");
        cc.setCoordX((float)8.51206);
        cc.setCoordY((float)49.43919);
        cc.setDureeService(2640);
        cc.setLibelle("C2");
        cc.setNumeroLieu("C2");
        cc.setNombreRemorquesMax(2);
        cc.setQuantiteVoulue(600);
        cc.setVille("Mannheim");
        lcc.add(cc);
        v.add(cc);
        
        System.out.println(cc.getNumeroLieu() + " : " + cc.getQuantiteVoulue());
        
        
        Algo.createFakeConsts();
        Constantes.capaciteMax = 500;
        
        System.out.println("Taille max remorques : " + Constantes.capaciteMax);
        
        Depot dp = Algo.getDepot();
        
        Radis r = new Radis(dp, lcc);
        
        System.out.println(r);
        
        Tomate t = r.generateInitialTomato();
        
        System.out.println(t);
        
        t = r.wololo();
        
        System.out.println(t);
        System.out.println(t.generateVehiculeAction());

    }
}
