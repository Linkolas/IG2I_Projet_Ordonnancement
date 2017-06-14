/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.cplex;

import java.util.ArrayList;
import java.util.List;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.Colis;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.SwapBody;
import ordo.data.entities.Trajet;
import ordo.data.entities.Vehicule;
import ordo.data.entities.VehiculeAction;

/**
 *
 * @author Nicolas
 */
public class DeCplexifier {
    
    public void CplexTourneesToSolution(ArrayList<CplexTournee> tournees) {
        JpaTrajetDao trajetDao = JpaTrajetDao.getInstance();
        JpaCommandeClientDao ccDao = JpaCommandeClientDao.getInstance();
        JpaVehiculeDao vehiDao = JpaVehiculeDao.getInstance();
        
        List<CommandeClient> commandes = new ArrayList<>();
        
        for(CplexTournee tournee: tournees) {
            Vehicule vehi = new Vehicule();
            
            if(tournee.needTrain()) {
                vehi.addSwapBody(new SwapBody());
            }
            
            Lieu prevLieu = null;
            for(Lieu lieu: tournee.getLieux()) {
                
                if(prevLieu == null) {
                    prevLieu = lieu;
                    continue;
                }
                
                Trajet trajet = trajetDao.find(prevLieu, lieu);
                
                VehiculeAction va = new VehiculeAction();
                va.setDepart(prevLieu);
                va.setArrivee(lieu);
                va.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
                va.setIsTrain(vehi.isTrain());
                va.setDistance(trajet.getDistance());
                va.setDuree(trajet.getDuree());
                vehi.addAction(va);
                
                if(lieu instanceof Depot) {
                    prevLieu = lieu;
                    break;
                }
                
                if(lieu instanceof CommandeClient) {
                    CommandeClient cc = (CommandeClient) lieu;
                    commandes.add(cc);
                    
                    VehiculeAction vat = new VehiculeAction();
                    vat.setDepart(lieu);
                    vat.setArrivee(lieu);
                    vat.setEnumAction(VehiculeAction.EnumAction.TRAITEMENT);
                    vat.setIsTrain(vehi.isTrain());
                    vat.setDistance(0);
                    vat.setDuree(cc.getDureeService());
                    vehi.addAction(vat);
                    
                    if(!vehi.isTrain()) {
                        Colis colis = new Colis();
                        colis.setCommande(cc);
                        colis.setQuantite(cc.getQuantiteVoulue());
                        
                        vehi.getSwapBodies().get(0).addColis(colis);
                    } else {
                        float qdispo_sw1 = Constantes.capaciteMax - vehi.getSwapBodies().get(0).getQuantite();
                        float qdispo_sw2 = Constantes.capaciteMax - vehi.getSwapBodies().get(1).getQuantite();
                        
                        if(qdispo_sw1 > cc.getQuantiteVoulue()) {
                            Colis colis = new Colis();
                            colis.setCommande(cc);
                            colis.setQuantite(cc.getQuantiteVoulue());

                            vehi.getSwapBodies().get(0).addColis(colis);
                            
                        } else {
                            
                            if(qdispo_sw1 > 0) {
                                Colis colis = new Colis();
                                colis.setCommande(cc);
                                colis.setQuantite(qdispo_sw1);

                                vehi.getSwapBodies().get(0).addColis(colis);
                            }
                            
                            float remaining = cc.getQuantiteVoulue() - qdispo_sw1;
                            
                            Colis colis2 = new Colis();
                            colis2.setCommande(cc);
                            colis2.setQuantite(remaining);
                            
                            vehi.getSwapBodies().get(1).addColis(colis2);
                        }
                        
                        
                    }
                    
                    prevLieu = lieu;
                    continue;
                }
                
                prevLieu = lieu;
            }
            
            vehiDao.create(vehi);
        }
        
        
        for(CommandeClient cc: ccDao.findAll()) {
            cc.setLivree(true);
            ccDao.update(cc);
        }
        
    }
    
}
