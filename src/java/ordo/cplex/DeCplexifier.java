/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.cplex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import static ordo.cplex.CplexSolve.generateTournees;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaDepotDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.Colis;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.SwapBody;
import ordo.data.entities.SwapLocation;
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
                    vehi.add(cc);
                    
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
                        
                        System.out.println("QDispo = " + qdispo_sw1);
                        System.out.println("QDispo = " + qdispo_sw2);
                        System.out.println("");
                        
                        if(qdispo_sw1 > cc.getQuantiteVoulue()) {
                            System.out.println("COLIS 1");
                            
                            Colis colis = new Colis();
                            colis.setCommande(cc);
                            colis.setQuantite(cc.getQuantiteVoulue());

                            vehi.getSwapBodies().get(0).addColis(colis);
                            
                        } else {
                            float remaining = cc.getQuantiteVoulue();
                            
                            if(qdispo_sw1 > 0) {
                                System.out.println("COLIS 1");
                                Colis colis = new Colis();
                                colis.setCommande(cc);
                                colis.setQuantite(qdispo_sw1);

                                vehi.getSwapBodies().get(0).addColis(colis);
                                
                                remaining = cc.getQuantiteVoulue() - qdispo_sw1;
                            }
                            
                            if(remaining > 0) {
                                System.out.println("COLIS 2");
                                Colis colis2 = new Colis();
                                colis2.setCommande(cc);
                                colis2.setQuantite(remaining);

                                vehi.getSwapBodies().get(1).addColis(colis2);
                            }
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
    
    
    public static void main(String[] args) {
        Constantes.capaciteMax = 500;
        
        List<CplexTournee> tournees = generateTournees();
        CplexSolve cp = new CplexSolve();
        for(CplexTournee ct: tournees) {
            cp.addTournee(ct);
        }
        cp.solve();
        ArrayList<CplexTournee> results = cp.getResults();
        System.out.println("DONE.");
        
        DeCplexifier dec = new DeCplexifier();
        dec.CplexTourneesToSolution(results);
        
    }
    
    public static List<CplexTournee> generateTournees() {
        
        JpaDepotDao daoDepot = JpaDepotDao.getInstance();
        JpaCommandeClientDao daoCc = JpaCommandeClientDao.getInstance();
        JpaSwapLocationDao daoSwLoc = JpaSwapLocationDao.getInstance();
        
        List<CplexTournee> tournees = new ArrayList<>();
        
        Depot d = daoDepot.findAll().iterator().next();
        SwapLocation sl = daoSwLoc.findAll().iterator().next();
        Iterator<CommandeClient> ccs = daoCc.findAll(false).iterator();
        CommandeClient c1 = ccs.next();
        CommandeClient c2 = ccs.next();
        
        daoDepot.create(d);
        daoSwLoc.create(sl);
        daoCc.create(c1);
        daoCc.create(c2);
        
        CplexTournee t1 = new CplexTournee();
        CplexTournee t2 = new CplexTournee();
        CplexTournee t3 = new CplexTournee();
        CplexTournee t4 = new CplexTournee();
        
        t1.addLieu(d);
        t2.addLieu(d);
        t3.addLieu(d);
        t4.addLieu(d);
        
        t1.addLieu(c1);
        t2.addLieu(c2);
        
        t3.addLieu(c1);
        t3.addLieu(sl);
        t3.addLieu(c2);
        
        t4.addLieu(c2);
        t4.addLieu(sl);
        t4.addLieu(c1);
        
        t1.addLieu(d);
        t2.addLieu(d);
        t3.addLieu(d);
        t4.addLieu(d);
        
        t1.setCost(10);
        t2.setCost(12);
        t3.setCost(18);
        t4.setCost(16);
        
        tournees.add(t1);
        tournees.add(t2);
        tournees.add(t3);
        tournees.add(t4);
        
        return tournees;
    }
}
