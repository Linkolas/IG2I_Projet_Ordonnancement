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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import ordo.data.dao.jpa.JpaTrajetDao;

/**
 *
 * @author Nicolas
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Lieu.findByCoordonnees", query="SELECT l FROM Lieu l WHERE l.coordX = :coordX AND l.coordY = :coordY"),
    @NamedQuery(name="Lieu.findByNumeroLieu", query="SELECT l FROM Lieu l WHERE l.numeroLieu = :numeroLieu")
    })
public class Lieu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column
    private String codePostal;
    @Column
    private String ville;
    @Column
    private float coordX;
    @Column
    private float coordY;
    @Column
    private String numeroLieu;

    public Lieu() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getNumeroLieu() {
        return numeroLieu;
    }

    public void setNumeroLieu(String numeroLieu) {
        this.numeroLieu = numeroLieu;
    }
    
    public float getCoordX() {
        return coordX;
    }

    public void setCoordX(float coordX) {
        this.coordX = coordX;
    }

    public float getCoordY() {
        return coordY;
    }

    public void setCoordY(float coordY) {
        this.coordY = coordY;
    }
    
    public float distanceToGoTo(Lieu o1){
        JpaTrajetDao    daoTrajet   = JpaTrajetDao.getInstance();
        Trajet t = daoTrajet.find(this, o1);
        return t.getDistance();
    }
    
    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 83 * hash + Objects.hashCode(this.codePostal);
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
        final Lieu other = (Lieu) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.codePostal, other.codePostal)) {
            return false;
        }
        return true;
    }

    
    
    // </editor-fold>

    @Override
    public String toString() {
        return "Lieu{" + "id=" + id + ", codePostal=" + codePostal + ", ville=" + ville + ", coordX=" + coordX + ", coordY=" + coordY + ", numeroLieu=" + numeroLieu + "}\n";
    }
}
