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
public class SwapBody implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private float quantite;
    @ManyToOne @JoinColumn(nullable = true)
    private Lieu lieu;
    @ManyToOne
    private Vehicule vehicule;
    @OneToMany(mappedBy = "swapBody", cascade={CascadeType.PERSIST})
    private List<Colis> colis = new ArrayList<>();

    public SwapBody() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public float getQuantite() {
        //return quantite;
        float rtn = 0;
        for(Colis c : this.colis){
            rtn += c.getQuantite();
        }
        return rtn;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
        
        if(!vehicule.getSwapBodies().contains(this)) {
            vehicule.addSwapBody(this);
        }
    }
    
    public List<Colis> getColis() {
        return colis;
    }

    public void addColis(Colis colis) {
        this.colis.add(colis);
        
        if(colis.getSwapBody() == null) {
            colis.setSwapBody(this);
        }
    }
    
    public void delColis(Colis colis) {
        this.colis.remove(colis);
    }

    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
        hash = 71 * hash + Float.floatToIntBits(this.quantite);
        hash = 71 * hash + Objects.hashCode(this.lieu);
        hash = 71 * hash + Objects.hashCode(this.vehicule);
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
        final SwapBody other = (SwapBody) obj;
        if (Float.floatToIntBits(this.quantite) != Float.floatToIntBits(other.quantite)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.lieu, other.lieu)) {
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
        return "SwapBody{" + "id=" + id + ", quantite=" + quantite + ", lieu=" + lieu + ", vehicule=" + vehicule + ", colis=" + colis + "}\n";
    }

}
