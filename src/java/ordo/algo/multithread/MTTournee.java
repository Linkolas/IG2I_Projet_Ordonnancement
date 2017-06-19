/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import ordo.algo.HypoTournee;
import ordo.cplex.CplexTournee;
import ordo.data.Constantes;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.Trajet;

/**
 *
 * @author Nicolas
 */
public class MTTournee extends CplexTournee {
    private float duree = 0;
    private float quantite = 0;
    private float distance = 0;
    private HashMap<MTTrajetKey, Trajet> trajets;
    
    enum Type {
        CAMION,
        TRAIN
    }
    private Type type = Type.CAMION;

    public MTTournee(Type type, HashMap<MTTrajetKey, Trajet> trajets) {
        this.type = type;
        this.trajets = trajets;
    }
    
    public float getDuree() {
        return duree;
    }

    public float getQuantite() {
        return quantite;
    }
    
    public float getDistance() {
        return distance;
    }
    
    public boolean isTooLong()
    {
        return (this.duree > Constantes.dureeMaxTournee);
    }
    
    public boolean isTooFull()
    {
        int coeff = (type == Type.TRAIN ? 2 : 1);
        float max = Constantes.capaciteMax * coeff;
        
        return (this.quantite > max);
    }
    
    /**
     * Donne le cout d'une tournée.
     * @return 
     */
    @Override
    public float getCost() {
        float cout = 0;
        
        cout += Constantes.coutCamion;
        cout += Constantes.coutDureeCamion * (this.getDuree()/3600);
        cout += Constantes.coutTrajetCamion * (this.getDistance()/1000);
        
        if(type == Type.TRAIN && getQuantite() > Constantes.capaciteMax) {
            cout += Constantes.coutSecondeRemorque;
            cout += Constantes.coutTrajetSecondeRemorque * (this.getDistance()/1000);
        }
        
        setCost(cout);
        
        return cout;
    }
    
    
    @Override
    public void addLieu(Lieu lieu) {
        if(!getLieux().isEmpty()) {
            // On récupère le lieu en fin de tournée, puis le trajet
            Lieu previous = getLieux().get(getLieux().size() -1);
            Trajet t = trajets.get(new MTTrajetKey(previous, lieu));

            duree += t.getDuree();
            distance += t.getDistance();
        }
        
        if(lieu instanceof CommandeClient) {
            CommandeClient cc = (CommandeClient) lieu;
            
            quantite += cc.getQuantiteVoulue();
            duree += cc.getDureeService();
        }
        
        // On ajoute le lieu à la toute fin
        super.addLieu(lieu);
    }
    
    @Override
    public void removeLieu(Lieu lieu) {
        // On supprime le lieu
        super.removeLieu(lieu);
        
        // Si il n'y a plus de lieux, on vide tout
        if(getLieux().isEmpty()) {
            duree = 0;
            distance = 0;
            quantite = 0;
            return;
        }
        
        // Sinon on récupère le lieu précédent et son trajet.
        Lieu previous = getLieux().get(getLieux().size() -1);
        
        Trajet t = trajets.get(new MTTrajetKey(previous, lieu));
        
        duree -= t.getDuree();
        distance -= t.getDistance();
        
        if(lieu instanceof CommandeClient) {
            CommandeClient cc = (CommandeClient) lieu;
            
            quantite -= cc.getQuantiteVoulue();
            duree -= cc.getDureeService();
            
        }
    }
    
    public void removeLastLieu() {
        
        if(getLieux().size() == 2) {
            System.out.println("WTF?");
        }
        
        Lieu lieu = getLieux().get(getLieux().size() -1);
        
        getLieux().remove(getLieux().size() -1);
        
        
        // Si il n'y a plus de lieux, on vide tout
        if(getLieux().isEmpty()) {
            duree = 0;
            distance = 0;
            quantite = 0;
            return;
        }
        
        // Sinon on récupère le lieu précédent et son trajet.
        Lieu previous = getLieux().get(getLieux().size() -1);
        Trajet t = trajets.get(new MTTrajetKey(previous, lieu));
        
        duree -= t.getDuree();
        distance -= t.getDistance();
        
        if(lieu instanceof CommandeClient) {
            CommandeClient cc = (CommandeClient) lieu;
            
            quantite -= cc.getQuantiteVoulue();
            duree -= cc.getDureeService();
        }
    }
    
    
    @Override
    public String toString() {
        
        String str = "Tournée : ";
        for(Lieu lieu: getLieux()) {
            if(lieu instanceof Depot) {
                str += "D ";
                continue;
            }
            
            if(lieu instanceof CommandeClient) {
                str += ((CommandeClient)lieu).getNumeroLieu() + " ";
                continue;
            }
        }
        
        return str;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.toString());
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
        final MTTournee other = (MTTournee) obj;
        if (!Objects.equals(this.toString(), other.toString())) {
            return false;
        }
        
        return true;
    }
}
