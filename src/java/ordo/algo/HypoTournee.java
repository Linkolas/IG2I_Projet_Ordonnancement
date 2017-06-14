/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo;

import ordo.cplex.CplexTournee;
import ordo.data.Constantes;

/**
 *
 * @author Axelle
 */
public class HypoTournee extends CplexTournee {
    private long duree;
    private float quantite;
    private long distance;

    public long getDuree() {
        return duree;
    }

    public void setDuree(long duree) {
        this.duree = duree;
    }

    public float getQuantite() {
        return quantite;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
    
    public boolean isTooLong()
    {
        return (this.duree > Constantes.dureeMaxTournee);
    }
    
    public boolean isTooFull()
    {
        return (this.quantite > (Constantes.capaciteMax*2));
    }
}
