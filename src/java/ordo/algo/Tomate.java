/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.SwapLocation;
import ordo.data.entities.Trajet;

/**
 *
 * @author Olivier
 */
public class Tomate {
    
    // Local Class
    public static class Chemin{
        // Attributs
        private Lieu depart;
        private Lieu arrivee;
        private float cost;
        private boolean needSwap;

        public Chemin(Lieu depart, Lieu arrivee, boolean needSwap, float cost) {
            this.depart = depart;
            this.arrivee = arrivee;
            this.needSwap = needSwap;
            this.cost = cost;
            if(cost == Float.NaN)
                cost = calcCost();
            
        }
        
        private float calcCost(){
            return JpaTrajetDao.getInstance().find(this.depart, this.arrivee).getDuree();
        }

        // <editor-fold defaultstate="collapsed" desc="Get Set toString">
        public Lieu getDepart() {
            return depart;
        }

        public void setDepart(Lieu depart) {
            this.depart = depart;
        }

        public Lieu getArrivee() {
            return arrivee;
        }

        public void setArrivee(Lieu arrivee) {
            this.arrivee = arrivee;
        }

        public boolean isNeedSwap() {
            return needSwap;
        }

        public void setNeedSwap(boolean needSwap) {
            this.needSwap = needSwap;
        }
        
        @Override
        public String toString() {
            return this.depart.getNumeroLieu() + " -> " + this.arrivee.getNumeroLieu() + " (" + this.cost + ")\n";
        }
        
        // </editor-fold>
        
        public boolean isOpositeOf(Chemin c1){
            if(c1.depart == this.arrivee && this.depart == c1.arrivee)
                return true;
            return false;
        }
    }
    
    // Liste des chemins
    List<Chemin> chemins;
    
    // Le swapLocation
    SwapLocation sl;

    // <editor-fold defaultstate="collapsed" desc="Get Set toString">
    public Tomate() {
        this.chemins = new ArrayList();
        this.sl = null;
    }

    public Tomate(SwapLocation sl) {
        this.sl = sl;
        this.chemins = new ArrayList();
    }

    public List<Chemin> getChemins() {
        return chemins;
    }

    public void setChemins(List<Chemin> chemins) {
        this.chemins = chemins;
    }

    public SwapLocation getSl() {
        return sl;
    }

    public void setSl(SwapLocation sl) {
        this.sl = sl;
    }

    @Override
    public String toString() {
        String rtn ="";
        for(Chemin c : this.chemins)
            rtn += '\t' + c.toString();
        return "Tomate \n" + rtn ;
    }
    
    // </editor-fold>
    
    /**
     * est-ce qu'il y a des liaisons Cx -> Cy | Cy -> Cx ?
     * @return La liste des chemins ayant des snakes
     */
    private List<Chemin> containsSnakes(){
        List<Chemin> lc = new ArrayList(); 
        for(Chemin c : this.chemins){
            for(Chemin c2 : this.chemins){
                if( c == c2) continue;
                if(c.isOpositeOf(c2))
                   lc.add(c);
            }
        }
        return lc;
    }
    
    public void addChemin(Chemin c){
        if(c.arrivee != null && c.depart != null){
            this.chemins.add(c);
        }
        else
            System.out.println("ERROR CHEMIN NULL");
    }
    
    public static void main(String[] args) {
        
    }
}