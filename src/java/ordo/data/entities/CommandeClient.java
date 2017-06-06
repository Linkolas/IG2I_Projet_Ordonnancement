/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
    @OneToMany(mappedBy = "commande", cascade={CascadeType.PERSIST})
    private List<Colis> colis  = new ArrayList<>();
    @Column
    private boolean isLivree = false;
    
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

    public List<Colis> getColis() {
        return colis;
    }

    public void addColis(Colis colis) {
        this.colis.add(colis);
    }
    
    public void delColis(Colis colis) {
        this.colis.remove(colis);
    }

    public boolean isLivree() {
        return isLivree;
    }

    public void setLivree(boolean isLivree) {
        this.isLivree = isLivree;
    }
    
    public int isCloserThan(Lieu l){
        //TODO Utiliser la notion de trajet pour comparer si un lieu est plus loin
        return 0;
    }
    
    public int isFutherThan(Lieu l){
        return -1*isCloserThan(l);
    }
    
    public float coutDuDetour(Lieu l1, Lieu l2){
        //retourne le cout pour réaliser un détour
        return 0;
    }
    
    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    @Override
    public String toString() {
        return "ordo.data.entities.Lieu[ id=" + getId() + " ]";
    }
    // </editor-fold>
}
