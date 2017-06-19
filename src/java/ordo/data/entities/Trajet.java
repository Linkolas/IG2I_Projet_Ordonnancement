/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Nicolas
 */
@Entity
@NamedQueries({
   @NamedQuery(name = "Trajet.getTrajet", query = "SELECT t FROM Trajet t WHERE t.depart.coordX = :departX AND t.depart.coordY = :departY AND t.destination.coordX = :destX AND t.destination.coordY = :destY")
})
public class Trajet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private int duree;
    @Column
    private int distance;
    @ManyToOne
    private Lieu depart;
    @ManyToOne
    private Lieu destination;

    public Trajet() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Lieu getDepart() {
        return depart;
    }

    public void setDepart(Lieu depart) {
        this.depart = depart;
    }

    public Lieu getDestination() {
        return destination;
    }

    public void setDestination(Lieu destination) {
        this.destination = destination;
    }
    

    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + this.duree;
        hash = 53 * hash + this.distance;
        hash = 53 * hash + Objects.hashCode(this.depart);
        hash = 53 * hash + Objects.hashCode(this.destination);
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
        final Trajet other = (Trajet) obj;
        if (this.duree != other.duree) {
            return false;
        }
        if (this.distance != other.distance) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.depart, other.depart)) {
            return false;
        }
        if (!Objects.equals(this.destination, other.destination)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "ordo.data.entities.Trajet[ id=" + id + " ]";
    }
    // </editor-fold>
}
