/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.util.Objects;
import ordo.data.entities.Lieu;
import ordo.data.entities.Trajet;

/**
 *
 * @author Nicolas
 */
public class MTTrajetKey {
    private Lieu depart, destination;

    public MTTrajetKey(Lieu depart, Lieu destination) {
        this.depart = depart;
        this.destination = destination;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Float.floatToIntBits(this.depart.getCoordX());
        hash = 47 * hash + Float.floatToIntBits(this.depart.getCoordY());
        hash = 47 * hash + Float.floatToIntBits(this.destination.getCoordX());
        hash = 47 * hash + Float.floatToIntBits(this.destination.getCoordY());
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
        final MTTrajetKey other = (MTTrajetKey) obj;
        if (Float.floatToIntBits(this.depart.getCoordX()) != Float.floatToIntBits(other.depart.getCoordX())) {
            return false;
        }
        if (Float.floatToIntBits(this.depart.getCoordY()) != Float.floatToIntBits(other.depart.getCoordY())) {
            return false;
        }
        if (Float.floatToIntBits(this.destination.getCoordX()) != Float.floatToIntBits(other.destination.getCoordX())) {
            return false;
        }
        if (Float.floatToIntBits(this.destination.getCoordY()) != Float.floatToIntBits(other.destination.getCoordY())) {
            return false;
        }
        return true;
    }

    
}
