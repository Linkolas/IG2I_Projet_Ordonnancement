/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author Nicolas
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name="CommandeClient.findAllCamions", 
            query="SELECT c FROM CommandeClient c WHERE c.nombreRemorquesMax=1"
    ),
    @NamedQuery(
            name="CommandeClient.findAllTrains",
            query="SELECT c FROM CommandeClient c WHERE c.nombreRemorquesMax=2"
    )
}) 
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
        
        if(!vehicule.getCommandes().contains(this)) {
            vehicule.add(this);
        }
    }

    public List<Colis> getColis() {
        return colis;
    }

    public void addColis(Colis colis) {
        this.colis.add(colis);
        
        if(colis.getCommande() != this) {
            colis.setCommande(this);
        }
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
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.libelle);
        hash = 79 * hash + this.nombreRemorquesMax;
        hash = 79 * hash + Float.floatToIntBits(this.quantiteVoulue);
        hash = 79 * hash + Float.floatToIntBits(this.dureeService);
        hash = 79 * hash + Objects.hashCode(this.vehicule);
        hash = 79 * hash + (this.isLivree ? 1 : 0);
        return hash;
    }

    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
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
        final CommandeClient other = (CommandeClient) obj;
        if (this.nombreRemorquesMax != other.nombreRemorquesMax) {
            return false;
        }
        if (Float.floatToIntBits(this.quantiteVoulue) != Float.floatToIntBits(other.quantiteVoulue)) {
            return false;
        }
        if (this.isLivree != other.isLivree) {
            return false;
        }
        if (!Objects.equals(this.libelle, other.libelle)) {
            return false;
        }
        if (!Objects.equals(this.vehicule, other.vehicule)) {
            return false;
        }
        return true;
    }

    // </editor-fold>
    @Override
    public String toString() {
        return "CommandeClient{" + "NUMERO LIEU" + this.getNumeroLieu() + ", libelle=" + libelle + ", nombreRemorquesMax=" + nombreRemorquesMax + ", quantiteVoulue=" + quantiteVoulue + ", dureeService=" + dureeService + ", vehicule=" + vehicule.getId() + ", colis=" + colis + ", isLivree=" + isLivree + "}\n";
    }
}
