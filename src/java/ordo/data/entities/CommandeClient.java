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

/**
 *
 * @author Nicolas
 */
@Entity
public class CommandeClient extends Lieu implements Serializable {

    @Column
    private String libelle;
    @Column
    private int nombreRemorquesMax = 1;
    @Column
    private float quantiteVoulue;
    @Column
    private float dureeService;
    @ManyToOne
    private Vehicule vehicule;
    
    public CommandeClient() {
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getNombreRemorquesMax() {
        return nombreRemorquesMax;
    }

    public void setNombreRemorquesMax(int nombreRemorquesMax) {
        this.nombreRemorquesMax = nombreRemorquesMax;
    }

    public float getQuantiteVoulue() {
        return quantiteVoulue;
    }

    public void setQuantiteVoulue(float quantiteVoulue) {
        this.quantiteVoulue = quantiteVoulue;
    }

    public float getDureeService() {
        return dureeService;
    }

    public void setDureeService(float dureeService) {
        this.dureeService = dureeService;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    @Override
    public String toString() {
        return "ordo.data.entities.Lieu[ id=" + getId() + " ]";
    }
    // </editor-fold>
}
