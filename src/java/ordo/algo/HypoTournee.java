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
    private float duree;
    private float quantite;
    private float distance;

    public float getDuree() {
        return duree;
    }

    public void setDuree(float duree) {
        this.duree = duree;
    }

    public float getQuantite() {
        return quantite;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
    
    public boolean isTooLong()
    {
        return (this.duree > Constantes.dureeMaxTournee);
    }
    
    public boolean isTooFull()
    {
        return (this.quantite > Constantes.capaciteMax);
    }
    
    /**
     * Donne le cout d'une tournée si elle ne contient que des clients camions
     * @return Le coût de la tournée
     */
    public float getCamionCost()
    {
        float cout = 0;
        cout += Constantes.coutCamion;
        cout += Constantes.coutDureeCamion * this.getDuree();
        cout += Constantes.coutTrajetCamion * this.getDistance();
        
        return cout;
    }
    
    /**
     * Donne le cout d'une tournée si elle ne contient que des clients trains
     * @return Le coût de la tournée
     */    
    public float getTrainCost()
    {
        float cout = 0;
        cout += Constantes.coutCamion;
        cout += Constantes.coutSecondeRemorque;
        
        cout += Constantes.coutDureeCamion * (this.getDuree()/3600);
        
        cout += Constantes.coutTrajetCamion * (this.getDistance()/1000);
        cout += Constantes.coutTrajetSecondeRemorque * (this.getDistance()/1000);
        
        return cout;
    }
}
