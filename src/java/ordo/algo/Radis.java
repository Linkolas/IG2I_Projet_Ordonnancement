/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.List;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.SwapLocation;

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

    /**
     * Constructeur par default.
     */
    public Radis() {
        lcc =   new ArrayList();
        sl  =   null;
        dp  =   null;
        c   =   null;
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
        
        this.dp     =   dp;
        this.lcc    =   lcc;
        this.sl     =   getNearestSwapLocation(lcc);
        this.c      =   generateCarotte(lcc, dp);
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
    
    private static Carotte generateCarotte(List<CommandeClient> lcc, Depot dp){
        return new Carotte(lcc, dp);
    }
    
}
