/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.cplex;

import java.util.ArrayList;
import ordo.data.entities.Lieu;

/**
 *
 * @author Nicolas
 */
public class CplexTournee {
    private ArrayList<Lieu> lieux = new ArrayList<>();
    private float cost = 0;

    public ArrayList<Lieu> getLieux() {
        return lieux;
    }

    public void addLieu(Lieu lieu) {
        this.lieux.add(lieu);
    }
    
    public void addLieu(int index, Lieu lieu) {
        this.lieux.add(index, lieu);
    }
    
    public void removeLieu(Lieu lieu) {
        this.lieux.remove(lieu);
    }
    
    public void resetLieux() {
        this.lieux = new ArrayList<>();
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}
