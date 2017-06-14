/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import ordo.data.entities.VehiculeAction;

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
            if(Float.isNaN(cost))
                this.cost = calcCost();
            
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.depart);
            hash = 71 * hash + Objects.hashCode(this.arrivee);
            hash = 71 * hash + Float.floatToIntBits(this.cost);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Chemin other = (Chemin) obj;
            if (Float.floatToIntBits(this.cost) != Float.floatToIntBits(other.cost)) {
                return false;
            }
            if (!Objects.equals(this.depart, other.depart)) {
                return false;
            }
            if (!Objects.equals(this.arrivee, other.arrivee)) {
                return false;
            }
            return true;
        }

        

        public float getCost() {
            return cost;
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
    public List<Chemin> containsSnakes(){
        List<Chemin> lc = new ArrayList(); 
        for(Chemin c : this.chemins){
            for(Chemin c2 : this.chemins){
                if( c == c2) continue;
                if(c.isOpositeOf(c2))
                   lc.add(c);
            }
        }
        if(this.chemins.size() == 2 && lc.size() == 2) return new ArrayList();
        return lc;
    }
    
    /**
     * Est-ce qu'il y a des liasons Cx -> Cy
     * @return 
     */
    public List<Chemin> containsTwins(){
        List<Chemin> lc = new ArrayList(); 
        for(Chemin c : this.chemins){
            for(Chemin c2 : this.chemins){
                if( c == c2) continue;
                if(c.getArrivee() == c2.getArrivee())
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
    
    public boolean lieuIsPresentInArrivals(Lieu l){
        for(Chemin c : this.chemins){
            if(c.arrivee == l)
                return true;
        }
        return false;
    }
    
    public List<VehiculeAction> generateVehiculeAction(){
        List<VehiculeAction> va = new ArrayList();
        VehiculeAction va_temp;
        for(Chemin c : this.chemins){
            va_temp = new VehiculeAction();
            va_temp.setDepart(c.depart);
            va_temp.setArrivee(c.arrivee);
            Trajet t = JpaTrajetDao.getInstance().find(c.depart, c.arrivee);
            va_temp.setDuree(t.getDuree());
            va_temp.setDistance(t.getDistance());
            va_temp.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
            va_temp.setIsTrain(c.isNeedSwap());
            va.add(va_temp);
            
            if(c.arrivee instanceof CommandeClient){
                va_temp = new VehiculeAction();
                va_temp.setDepart(c.arrivee);
                va_temp.setArrivee(c.arrivee);
                va_temp.setDuree(((CommandeClient)c.arrivee).getDureeService() );
                va_temp.setDistance(0);
                va_temp.setEnumAction(VehiculeAction.EnumAction.TRAITEMENT);
                va_temp.setIsTrain(c.isNeedSwap());
                va.add(va_temp);
            }
        }
        return va;
    } 
    
    public void replace(Chemin c1, Chemin c2){
        int index = this.getChemins().indexOf(c1);
        int index2 = chemins.indexOf(chemins.get(0));
        int index3 = chemins.indexOf(chemins.get(1));
        int index4 = chemins.indexOf(chemins.get(2));
        this.getChemins().remove(c1);
        this.getChemins().add(index, c2);
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
        
        Tomate t = new Tomate(sl);
        
        // On simule un snake
        Chemin c = new Chemin(v.getCommandes().get(0), v.getCommandes().get(1), true, Float.NaN);
        t.addChemin(c);
        c = new Chemin(v.getCommandes().get(1), v.getCommandes().get(0), true, Float.NaN);
        t.addChemin(c);
        
        // C1 -> C2 (4230.0)
	// C2 -> C1 (4050.0)
        
        System.out.print("La tomate contient un snake ? : ");
        
        if(!t.containsSnakes().isEmpty())
            System.out.println("OUI");
        else
            System.out.println("NON");
        
        System.out.print("La tomate contient des jumaux ? : ");
        
        if(!t.containsTwins().isEmpty())
            System.out.println("OUI");
        else
            System.out.println("NON");
        
        System.out.println(t);
    }
}