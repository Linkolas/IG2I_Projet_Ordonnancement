/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Nicolas
 */
@Entity
public class Depot extends Lieu implements Serializable {

    public Depot() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=".equals, .toString, ...">
    @Override
    public String toString() {
        return "ordo.data.entities.Depot[ id=" + getId() + " ]";
    }
    //</editor-fold>
}
