/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
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
public class Solution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private String libelle;
    @OneToMany(mappedBy = "solution")
    private List<Vehicule> vehicules;
    
    public Solution() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<Vehicule> getVehicules() {
        return vehicules;
    }

    public void setVehicules(List<Vehicule> vehicules) {
        this.vehicules = vehicules;
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
        if (!(object instanceof Solution)) {
            return false;
        }
        Solution other = (Solution) object;
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

    
    private float capaciteMax = -1;
    private float dureeMaxTournee = -1;
    
    private float dureePark = -1;
    private float dureePickup = -1;
    private float dureeSwap = -1;
    private float dureeExchange = -1;
    
    private float coutCamion = -1;
    private float coutDureeCamion = -1;
    private float coutTrajetCamion = -1;
    private float coutSecondeRemorque = -1;
    private float coutTrajetSecondeRemorque = -1;

    public float getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(float capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public float getDureeMaxTournee() {
        return dureeMaxTournee;
    }

    public void setDureeMaxTournee(float dureeMaxTournee) {
        this.dureeMaxTournee = dureeMaxTournee;
    }

    public float getDureePark() {
        return dureePark;
    }

    public void setDureePark(float dureePark) {
        this.dureePark = dureePark;
    }

    public float getDureePickup() {
        return dureePickup;
    }

    public void setDureePickup(float dureePickup) {
        this.dureePickup = dureePickup;
    }

    public float getDureeSwap() {
        return dureeSwap;
    }

    public void setDureeSwap(float dureeSwap) {
        this.dureeSwap = dureeSwap;
    }

    public float getDureeExchange() {
        return dureeExchange;
    }

    public void setDureeExchange(float dureeExchange) {
        this.dureeExchange = dureeExchange;
    }

    public float getCoutCamion() {
        return coutCamion;
    }

    public void setCoutCamion(float coutCamion) {
        this.coutCamion = coutCamion;
    }

    public float getCoutDureeCamion() {
        return coutDureeCamion;
    }

    public void setCoutDureeCamion(float coutDureeCamion) {
        this.coutDureeCamion = coutDureeCamion;
    }

    public float getCoutTrajetCamion() {
        return coutTrajetCamion;
    }

    public void setCoutTrajetCamion(float coutTrajetCamion) {
        this.coutTrajetCamion = coutTrajetCamion;
    }

    public float getCoutSecondeRemorque() {
        return coutSecondeRemorque;
    }

    public void setCoutSecondeRemorque(float coutSecondeRemorque) {
        this.coutSecondeRemorque = coutSecondeRemorque;
    }

    public float getCoutTrajetSecondeRemorque() {
        return coutTrajetSecondeRemorque;
    }

    public void setCoutTrajetSecondeRemorque(float coutTrajetSecondeRemorque) {
        this.coutTrajetSecondeRemorque = coutTrajetSecondeRemorque;
    }
    
    
}
