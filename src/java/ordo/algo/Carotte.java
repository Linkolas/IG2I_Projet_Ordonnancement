/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.SwapBody;
import ordo.data.entities.SwapLocation;
import ordo.data.entities.Trajet;
import ordo.data.entities.Vehicule;

/**
 *
 * @author Olivier
 */
public class Carotte {
    
    //Attributs
    
    // CommandeClients
    List<CommandeClient> lcc;
    
    // La Matrice
    //   ROW         COL     VAL
    Map<String, Map<String, Float>> matrice;

    public Carotte() {
        lcc = null;
        matrice = null;
    }
    
    public Carotte(List<CommandeClient> lcc, Depot dp) {
        this.lcc = lcc;
        matrice = new HashMap();
        
        JpaTrajetDao trajetDao = JpaTrajetDao.getInstance();
        
        genRowDepot(dp);
        
        for(CommandeClient cc_row : lcc){
            if(cc_row.getVehicule().getSwapBodies().size() == 1 ){
                // On est un véhicule camion on ne passe donc pas par les swapLocation
                clear();
                return;
            }
            matrice.put(cc_row.getNumeroLieu(), new HashMap());
            for(CommandeClient cc_col : lcc){
               if (cc_row == cc_col){
                   none(cc_row, cc_col);
               }
               else{
                   // Est-ce que le client de départ est un train et le camion d'arrivé un camion et que la commande du client train est sup à Q
                   if(  cc_row.getNombreRemorquesMax() > 1 && cc_col.getNombreRemorquesMax() == 1 && cc_row.getQuantiteVoulue()> Constantes.capaciteMax){
                       // On a donc forcément 2 swapbody on doit donc en delaisser un pour y aller
                       park(cc_row, cc_col);
                   }
                   
                   // Est ce que le client que je dois aller visiter à une commande > Q (Train) est je part de chez un camion
                   else if(cc_row.getNombreRemorquesMax() == 1 && cc_col.getNombreRemorquesMax() > 1 && cc_col.getQuantiteVoulue()> Constantes.capaciteMax){
                       // On a donc forcément 2 swapbody on doit donc en delaisser un pour y aller
                       pickup(cc_row, cc_col);
                   }
                   else{
                       none(cc_row, cc_col);
                   }
               }
            }
            
            // On effectue le même le test mais pour le dépot
            
            
            // Est-ce que le client de départ est un train et le camion d'arrivé un camion et que la commande du client train est sup à Q
            if(  cc_row.getNombreRemorquesMax() > 1 && cc_row.getQuantiteVoulue()> Constantes.capaciteMax){
                this.matrice.get(cc_row.getNumeroLieu()).put("D1", (float)0);
            }

            // Est ce que le client que je dois aller visiter à une commande > Q (Train) est je part de chez un camion
            else if(cc_row.getNombreRemorquesMax() == 1){
                // On a donc forcément 2 swapbody on doit donc en delaisser un pour y aller
                this.matrice.get(cc_row.getNumeroLieu()).put("D1", (float)1);
            }
            else{
                this.matrice.get(cc_row.getNumeroLieu()).put("D1", (float)0);
            }
        }
    }
    
    public boolean needSwapLocation(Lieu l1, Lieu l2){
        if(this.matrice.get(l1.getNumeroLieu()).get(l2.getNumeroLieu()) == 0)
            return false;
        return true;
    }
    
    private void none(Lieu start, Lieu end){
        this.matrice.get(start.getNumeroLieu()).put(end.getNumeroLieu(), (float)0);
    }
    
    /**
     * Précise que si on souhaite aller de @param start vers @end
     * On est obligé de deposer un swapBody
     * @param start
     * @param end 
     */
    private void park(Lieu start, Lieu end){
        this.matrice.get(start.getNumeroLieu()).put(end.getNumeroLieu(), (float)-1);
    }
    
    /**
     * Précise que si on souhaite aller de @param start vers @end
     * On est obligé de prendre un swapBody
     * @param start
     * @param end 
     */
    private void pickup(Lieu start, Lieu end){
        this.matrice.get(start.getNumeroLieu()).put(end.getNumeroLieu(), (float)1);
    }

    /**
     * Met toutes les valeurs à 0.
     */
    private void clear() {
        for(CommandeClient cc_row : lcc){
            matrice.put(cc_row.getNumeroLieu(), new HashMap());
            for(CommandeClient cc_col : lcc){
               none(cc_row, cc_col);
            }
            this.matrice.get(cc_row.getNumeroLieu()).put("D1", (float)0);
        }
    }

    @Override
    public String toString() {
        String rtn = "\n//// MATRICE CAROTTE ////\n";
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
        return rtn;
    }
    
    /**
     * Ajout manuelle de la ligne de dépot 
     * @param dp 
     */
    private void genRowDepot(Depot dp) {
        matrice.put("D1", new HashMap());
        matrice.get("D1").put("D1", (float)0);
        for(CommandeClient cc_col : this.lcc){
            //Si on demarre à deux swap et que l'on va vers un client camion on doit déposer le colis
            if(cc_col.getNombreRemorquesMax() == 1 && cc_col.getVehicule().isTrain()){
                this.matrice.get("D1").put(cc_col.getNumeroLieu(), (float)-1);
            }
            else{
                this.matrice.get("D1").put(cc_col.getNumeroLieu(), (float)0);
            }
        }
    }
    
    public static void main(String[] args) {
        // On get la Dao
        JpaCommandeClientDao    daoCommandeClient   = JpaCommandeClientDao.getInstance();
        
        //On fake des données
        
        List<CommandeClient> lcc = new ArrayList();
        
        Vehicule v = new Vehicule();
        //v.addSwapBody(new SwapBody());
        
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
        cc.setNombreRemorquesMax(1);
        cc.setQuantiteVoulue(100);
        cc.setVille("Mannheim");
        lcc.add(cc);
        v.add(cc);
        
        System.out.println(cc.getNumeroLieu() + " : " + cc.getQuantiteVoulue());
        
        cc = new CommandeClient();
        cc.setCodePostal("55606");
        cc.setCoordX((float)7.44494);
        cc.setCoordY((float)49.79178);
        cc.setDureeService(1980);
        cc.setLibelle("C3");
        cc.setNumeroLieu("C3");
        cc.setNombreRemorquesMax(1);
        cc.setQuantiteVoulue(250);
        cc.setVille("Kirn");
        lcc.add(cc);
        v.add(cc);
        
        System.out.println(cc.getNumeroLieu() + " : " + cc.getQuantiteVoulue());
        
        Algo.createFakeConsts();
        Constantes.capaciteMax = 500;
        
        System.out.println("Taille max remorques : " + Constantes.capaciteMax);
        
        Depot dp = Algo.getDepot();
        
        Carotte c = new Carotte(lcc, dp);
        
        
        
        System.out.println(c);
    }
    
    
}