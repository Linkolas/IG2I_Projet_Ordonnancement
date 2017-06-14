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
        cout += Constantes.coutTrajetCamion * this.getQuantite();
        
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
        
        cout += Constantes.coutDureeCamion * this.getDuree();
        
        cout += Constantes.coutTrajetCamion * this.getQuantite();
        cout += Constantes.coutTrajetSecondeRemorque * this.getQuantite();
        
        return cout;
    }
}
