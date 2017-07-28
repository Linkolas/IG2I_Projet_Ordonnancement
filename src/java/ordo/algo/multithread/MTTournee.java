/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    public MTTournee(MTTournee tournee, HashMap<MTTrajetKey, Trajet> trajets) {
        this.duree = tournee.getDuree();
        this.distance = tournee.getDistance();
        this.quantite = tournee.getQuantite();
        this.type = tournee.getType();
        this.trajets = trajets;
        
        for(Lieu lieu: tournee.getLieux()) {
            addLieu(lieu);
        }
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
    
    public void setType(Type type){
        this.type = type;
    }
    
    public Type getType() {
        return this.type;
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
    
    public boolean containsClientCamion() {
        for(Lieu lieu : getLieux()) {
            if(lieu instanceof CommandeClient) {
                CommandeClient cc = (CommandeClient) lieu;
                if(cc.getNombreRemorquesMax() == 1) {
                    return true;
                }
            }
        }
        
        return false;
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
    
    private float getCost(float addDuree, float addDistance) {
        float cout = 0;
        
        float newDuree = this.getDuree() + addDuree;
        float newDistance = this.getDistance() + addDistance;
        
        cout += Constantes.coutCamion;
        cout += Constantes.coutDureeCamion * (newDuree / 3600);
        cout += Constantes.coutTrajetCamion * (newDistance / 1000);
        
        if(type == Type.TRAIN && getQuantite() > Constantes.capaciteMax) {
            cout += Constantes.coutSecondeRemorque;
            cout += Constantes.coutTrajetSecondeRemorque * (newDistance / 1000);
        }
        
        return cout;
    }
    
    public void addLieuBestInsertion(Lieu lieu) {
        if(lieu instanceof Depot) {
            System.out.println("WARNING: BestInsertion of DEPOT !");
            addLieu(lieu);
            return;
        }
        
        int ibest = 1;
        
        if(!getLieux().isEmpty()) {
            
            float bestCost = -1;
            
            Lieu firstLieu = getLieux().get(0);
            Lieu lastLieu = getLieux().get(getLieux().size()-1);
            
            if(!(firstLieu instanceof Depot)) {
                Trajet tmp = trajets.get(new MTTrajetKey(lieu, firstLieu));
                float currentCost = getCost(tmp.getDuree(), tmp.getDistance());
                
                if(currentCost < bestCost || bestCost == -1) {
                    ibest = 0;
                }
            }
            
            if(!(lastLieu instanceof Depot)) {
                Trajet tmp = trajets.get(new MTTrajetKey(lastLieu, lieu));
                float currentCost = getCost(tmp.getDuree(), tmp.getDistance());
                
                if(currentCost < bestCost || bestCost == -1) {
                    ibest = getLieux().size();
                }
            }
            
            if(getLieux().size() > 1) {
                
                for(int i = 0; i < getLieux().size()-2; i++) {
                    Lieu before = getLieux().get(i);
                    Lieu after = getLieux().get(i+1);
                    
                    if(after instanceof Depot) {
                        break;
                    }
                    
                    Trajet oldTrajet = trajets.get(new MTTrajetKey(before, after));
                    Trajet t1 = trajets.get(new MTTrajetKey(before, lieu));
                    Trajet t2 = trajets.get(new MTTrajetKey(lieu, after));
                    
                    float addDuree = t1.getDuree() + t2.getDuree() - oldTrajet.getDuree();
                    float addDistance = t1.getDistance() + t2.getDistance() - oldTrajet.getDistance();
                    float currentCost = getCost(addDuree, addDistance);
                    
                    if(currentCost < bestCost || bestCost == -1) {
                        ibest = i+1;
                    }
                }
            }

            if(ibest == 0) {
                Trajet tmp = trajets.get(new MTTrajetKey(lieu, firstLieu));
                duree += tmp.getDuree();
                distance += tmp.getDistance();
                
            } else
            if(ibest == getLieux().size()) {
                Trajet tmp = trajets.get(new MTTrajetKey(lastLieu, lieu));
                duree += tmp.getDuree();
                distance += tmp.getDistance();
                
            } else { // on insère le lieu entre deux autres
                
                Lieu before = getLieux().get(ibest-1);
                Lieu after = getLieux().get(ibest);

                Trajet oldTrajet = trajets.get(new MTTrajetKey(before, after));
                Trajet t1 = trajets.get(new MTTrajetKey(before, lieu));
                Trajet t2 = trajets.get(new MTTrajetKey(lieu, after));

                duree += t1.getDuree() + t2.getDuree() - oldTrajet.getDuree();
                distance += t1.getDistance() + t2.getDistance() - oldTrajet.getDistance();
            }
        }
        
        if(lieu instanceof CommandeClient) {
            CommandeClient cc = (CommandeClient) lieu;
            
            quantite += cc.getQuantiteVoulue();
            duree += cc.getDureeService();
        }
        
        // On ajoute le lieu à la toute fin
        super.addLieu(ibest, lieu);
    }
    
    @Override
    public void removeLieu(Lieu lieu) {
        
        int size = getLieux().size();
        int index = getLieux().indexOf(lieu);
        
        if(index < 0) {
            return;
        }
        
        if(size > 1) {
        
            if(index == 0) {
                Lieu after = getLieux().get(1);
                Trajet t = trajets.get(new MTTrajetKey(lieu, after));

                duree -= t.getDuree();
                distance -= t.getDistance();

            } else
            if(index == size-1) {
                Lieu before = getLieux().get(size -2);
                Trajet t = trajets.get(new MTTrajetKey(before, lieu));

                duree -= t.getDuree();
                distance -= t.getDistance();

            } else {

                Lieu before = getLieux().get(index -1);
                Lieu after = getLieux().get(index +1);

                Trajet newTrajet = trajets.get(new MTTrajetKey(before, after));
                Trajet t1 = trajets.get(new MTTrajetKey(before, lieu));
                Trajet t2 = trajets.get(new MTTrajetKey(lieu, after));

                duree += newTrajet.getDuree() - t1.getDuree() - t2.getDuree();
                distance += newTrajet.getDistance() - t1.getDistance() - t2.getDistance();

            }
        }
        
        if(lieu instanceof CommandeClient) {
            CommandeClient cc = (CommandeClient) lieu;
            
            quantite -= cc.getQuantiteVoulue();
            duree -= cc.getDureeService();
        }
        
        
        // On supprime le lieu
        super.removeLieu(lieu);
        
        // Si il n'y a plus de lieux, on vide tout
        if(getLieux().isEmpty()) {
            duree = 0;
            distance = 0;
            quantite = 0;
            return;
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
