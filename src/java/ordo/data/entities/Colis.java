/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import java.util.Objects;
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
public class Colis implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private float quantite;
    @ManyToOne
    private SwapBody swapBody;
    @ManyToOne
    private CommandeClient commande;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getQuantite() {
        return quantite;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    public SwapBody getSwapBody() {
        return swapBody;
    }

    public void setSwapBody(SwapBody swapBody) {
        this.swapBody = swapBody;
        
        if(!swapBody.getColis().contains(this)) {
            swapBody.addColis(this);
        }
    }

    public CommandeClient getCommande() {
        return commande;
    }

    public void setCommande(CommandeClient commande) {
        this.commande = commande;
        
        if(!commande.getColis().contains(this)) {
            commande.addColis(this);
        }
    }

    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">

    
    
    // </editor-fold>

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Colis other = (Colis) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
