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
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.SwapBody;
import ordo.data.entities.SwapLocation;
import ordo.data.entities.Vehicule;

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
    }
}
