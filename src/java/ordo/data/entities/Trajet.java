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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Nicolas
 */
@Entity
@NamedQueries({
   @NamedQuery(name = "Trajet.getTrajet", query = "SELECT t FROM Trajet t WHERE t.depart = :id_depart AND t.destination = :id_destination")
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Trajet)) {
            return false;
        }
        Trajet other = (Trajet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
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
