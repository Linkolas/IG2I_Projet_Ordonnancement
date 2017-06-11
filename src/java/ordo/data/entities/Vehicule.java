/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Nicolas
 */
@Entity
public class Vehicule implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany(mappedBy = "vehicule", cascade={CascadeType.PERSIST})
    private List<SwapBody> swapBodies = new ArrayList<>();
    @OneToMany(mappedBy = "vehicule", cascade={CascadeType.MERGE})
    private List<CommandeClient> commandes = new ArrayList<>();
    @OneToMany(mappedBy = "vehicule", cascade={CascadeType.PERSIST})
    private List<VehiculeAction> actions = new ArrayList<>();
    @ManyToOne
    private Solution solution;
    
    public Vehicule() {
        this.addSwapBody(new SwapBody()); // On creer au moins une remorque
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public float getQuantity(){
        return getQuantity(false);
    }
    
    public float getQuantity(boolean onlyNotLivre){
        float rtn = 0;
        for(SwapBody sb: swapBodies) {
            for(Colis c: sb.getColis()) {
                if(onlyNotLivre && c.getCommande().isLivree()) {
                    continue;
                }
                rtn += c.getQuantite();
            }
        }
        return rtn;
    }

    public List<SwapBody> getSwapBodies() {
        return swapBodies;
    }
    
    public void addSwapBody(SwapBody swapBody) {
        if(swapBody.getVehicule() != this)
            swapBody.setVehicule(this);
        swapBodies.add(swapBody);
    }
    
    public void delSwapBody(SwapBody swapBody) {
        swapBodies.remove(swapBody);
    }

    public float getDistanceParcourue() {
        float distance = 0;
        for(VehiculeAction va: actions) {
            distance += va.getDistance();
        }
        return distance;
    }

    public float getDistanceParcourue_train() {
        
        float distance = 0;
        for(VehiculeAction va: actions) {
            if(!va.isIsTrain()) {
                continue;
            }
            distance += va.getDistance();
        }
        return distance;
    }
    
    public float getTempsTrajet() {
        float temps = 0;
        for(VehiculeAction va: actions) {
            temps += va.getDuree();
        }
        return temps;
    }

    public List<CommandeClient> getCommandes() {
        return commandes;
    }
    
    public void add(CommandeClient cc){
        commandes.add(cc);
        
        if(cc.getVehicule() != this){
            cc.setVehicule(this);
        }
    }
    
    public void delCommande(CommandeClient cc) {
        commandes.remove(cc);
    }

    public List<VehiculeAction> getActions() {
        return actions;
    }
    
    public void addAction(VehiculeAction action) {
        actions.add(action);
        
        if(action.getVehicule() != this) {
            action.setVehicule(this);
        }
    }
    
    public void delAction(VehiculeAction action) {
        actions.remove(action);
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
    
    public boolean isTrain() {
        return (swapBodies.size() > 1);
    }
    
    public boolean isCamion(){
        return (this.swapBodies.size() == 1 );
    }
    
    public void addSmartly(CommandeClient cc){
        for(CommandeClient _cc : this.getCommandes()){
            if(_cc.isCloserThan(cc) >= 0){
                
            }
        }
    }
    

    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Vehicule other = (Vehicule) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    
    // </editor-fold>

    @Override
    public String toString() {
        return "Vehicule{" + "id=" + id + ", distanceParcourue=" + getDistanceParcourue() + ", distanceParcourue_train=" + getDistanceParcourue_train() + ", tempsTrajet=" + getTempsTrajet() + ", swapBodies=" + swapBodies.size() + ", commandes=" + commandes + ", actions=" + actions + ", solution=" + solution + '}';
    }

}
