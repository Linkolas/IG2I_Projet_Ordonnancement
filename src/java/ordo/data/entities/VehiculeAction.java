/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

/**
 *
 * @author Nicolas
 */
@Entity
@NamedQuery(name="VehiculeAction.findByVehicule", query="SELECT v FROM VehiculeAction v WHERE v.vehicule = :vehicule ") 
public class VehiculeAction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private EnumAction enumAction;
    @Column
    private float duree;
    @Column
    private float distance;
    @ManyToOne
    private Vehicule vehicule;
    @ManyToOne
    private Lieu depart;
    @ManyToOne
    private Lieu arrivee;
    @Column
    private boolean isTrain = false;
    
    
    public VehiculeAction() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumAction getEnumAction() {
        return enumAction;
    }

    public void setEnumAction(EnumAction enumAction) {
        this.enumAction = enumAction;
    }

    public float getDuree() {
        return duree;
    }

    public void setDuree(float duree) {
        this.duree = duree;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
        
        if(!vehicule.getActions().contains(this)) {
            vehicule.addAction(this);
        }
    }

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

    public boolean isIsTrain() {
        return isTrain;
    }

    public void setIsTrain(boolean isTrain) {
        this.isTrain = isTrain;
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
        if (!(object instanceof VehiculeAction)) {
            return false;
        }
        VehiculeAction other = (VehiculeAction) object;
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

    public static enum EnumAction {
        DEPLACEMENT,
        TRAITEMENT,
        PARK,
        PICKUP,
        SWAP,
        EXCHANGE,
    }
    
}
