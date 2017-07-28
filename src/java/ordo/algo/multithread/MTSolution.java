/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Lieu;

/**
 *
 * @author Nicolas
 */
public class MTSolution {
    public List<MTTournee> tournees = new ArrayList<>();
    
    
    public float getCost() {
        float cost = 0;
        for(MTTournee tournee : tournees) {
            cost += tournee.getCost();
        }
        return cost;
    }
    
    public boolean isValid(int nbClients) {
        Map<CommandeClient, Integer> counts = new HashMap<>();
        
        for(MTTournee tournee: tournees) {
            for(Lieu lieu : tournee.getLieux()) {
                if(lieu instanceof CommandeClient) {
                    CommandeClient cc = (CommandeClient) lieu;
                    
                    int count = counts.getOrDefault(cc, 0) +1;
                    
                    if(count > 1) {
                        return false;
                    }
                    
                    counts.put(cc, count);
                }
            }
        }
        
        if(counts.size() != nbClients) {
            return false;
        }
        
        return true;
    }
}
