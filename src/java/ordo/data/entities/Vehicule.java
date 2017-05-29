/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private float distanceParcourue;
    @Column
    private float distanceParcourue_train;
    @Column
    private float tempsTrajet;
    @OneToMany(mappedBy = "vehicule")
    private List<SwapBody> swapBodies;
    @OneToMany(mappedBy = "vehicule")
    private List<CommandeClient> commandes;
    @OneToMany(mappedBy = "vehicule")
    private List<VehiculeAction> actions;
    @ManyToOne
    private Solution solution;
    
    public Vehicule() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SwapBody> getSwapBodies() {
        return swapBodies;
    }
    
    public void add(CommandeClient cc){
        if(cc != null && !commandes.contains(cc))
            commandes.add(cc);
    }
    
    public float getQuantity(){
        float rtn = 0;
        for(Iterator<CommandeClient> iter = this.commandes.iterator(); iter.hasNext(); ){
            CommandeClient cc = iter.next();
            rtn+= cc.getQuantiteVoulue();
        }
        return rtn;
    }

    public void setSwapBodies(List<SwapBody> swapBodies) {
        this.swapBodies = swapBodies;
    }

    public float getDistanceParcourue() {
        return distanceParcourue;
    }

    public void setDistanceParcourue(float distanceParcourue) {
        this.distanceParcourue = distanceParcourue;
    }

    public float getDistanceParcourue_train() {
        return distanceParcourue_train;
    }

    public void setDistanceParcourue_train(float distanceParcourue_train) {
        this.distanceParcourue_train = distanceParcourue_train;
    }

    public float getTempsTrajet() {
        return tempsTrajet;
    }

    public void setTempsTrajet(float tempsTrajet) {
        this.tempsTrajet = tempsTrajet;
    }

    public List<CommandeClient> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<CommandeClient> commandes) {
        this.commandes = commandes;
    }

    public List<VehiculeAction> getActions() {
        return actions;
    }

    public void setActions(List<VehiculeAction> actions) {
        this.actions = actions;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
    

    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    private static final long serialVersionUID = 1L;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vehicule)) {
            return false;
        }
        Vehicule other = (Vehicule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ordo.data.entities.SwapBody[ id=" + id + " ]";
    }
    // </editor-fold>

}
