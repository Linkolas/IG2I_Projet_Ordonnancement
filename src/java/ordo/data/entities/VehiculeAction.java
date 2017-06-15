/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
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
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.enumAction);
        hash = 83 * hash + Float.floatToIntBits(this.duree);
        hash = 83 * hash + Float.floatToIntBits(this.distance);
        hash = 83 * hash + Objects.hashCode(this.vehicule);
        hash = 83 * hash + Objects.hashCode(this.depart);
        hash = 83 * hash + Objects.hashCode(this.arrivee);
        hash = 83 * hash + (this.isTrain ? 1 : 0);
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
        final VehiculeAction other = (VehiculeAction) obj;
        if (Float.floatToIntBits(this.duree) != Float.floatToIntBits(other.duree)) {
            return false;
        }
        if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(other.distance)) {
            return false;
        }
        if (this.isTrain != other.isTrain) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.enumAction != other.enumAction) {
            return false;
        }
        if (!Objects.equals(this.vehicule, other.vehicule)) {
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
    
    
    

    @Override
    public String toString() {
        return "VehiculeAction{" + "enumAction=" + enumAction + ", duree=" + duree + ", distance=" + distance + ", depart=" + depart + ", arrivee=" + arrivee + ", isTrain=" + isTrain + "}\n";
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
    
    /**
     * Permet de comparer deux VehiculeAction en fonction de leur ID
     * Ceci permet lors de l'écriture du CSV de récupérer les VehiculeAction dans le bon ordre
     */
    public static Comparator<VehiculeAction> VehiculeActionIdComparator = new Comparator<VehiculeAction>() {
        @Override
        public int compare(VehiculeAction action1, VehiculeAction action2)
        {
            if(action1.getId() > action2.getId())
            {
                return 1;
            }
            return -1;
        }

    };
}
