/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.jsprit;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ordo.algo.Algo;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.Colis;
import ordo.data.entities.Trajet;
import ordo.data.entities.Vehicule;
import ordo.data.entities.VehiculeAction;

/**
 *
 * @author Nicolas
 */
public class RunWithoutSwapLocation {
    
    public static Collection<VehicleRoute> calc(Collection<CommandeClient> ccc, Depot d){
        /*
         * On creer un fichier de sortie pour stocker la solution
	 */
        File dir = new File("output");
        // if the directory does not exist, create it
        if (!dir.exists()) {
            System.out.println("creating directory ./output");
            boolean result = dir.mkdir();
            if (result) System.out.println("./output created");
        }
        
        /**
         * Create Depot location
         */
        Location depot_Location = Location.Builder.newInstance().setId(d.getNumeroLieu()).setCoordinate(Coordinate.newInstance(d.getCoordX(), d.getCoordY())).build();
        
        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and one capacity dimension, i.e. weight, and capacity dimension value of 2
	 */
        final int WEIGHT_INDEX = 0;
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .addCapacityDimension(WEIGHT_INDEX, 500)
                .setCostPerDistance((double)Constantes.coutTrajetCamion/(double)1000)
                .setCostPerServiceTime((double)Constantes.coutDureeCamion/(double)3600)
                .setCostPerWaitingTime(0)
                .setFixedCost(Constantes.coutCamion)
                .setCostPerTransportTime((double)Constantes.coutDureeCamion/(double)3600);
                
        VehicleType vehicleType = vehicleTypeBuilder.build();
        
        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle")
            .setStartLocation(depot_Location).setType(vehicleType)
            .setEarliestStart(0)
            .setLatestArrival(Constantes.dureeMaxTournee)
            .build();
        
        List<Service> ls = new ArrayList();
        /**
         * Pour chaque commandes client on créé un service
         */
        for(CommandeClient cc : ccc){
            Service s = Service.Builder.newInstance(cc.getNumeroLieu())
                    .addSizeDimension(WEIGHT_INDEX, (int) cc.getQuantiteVoulue())
                    .setLocation(Location.Builder.newInstance().setId(cc.getNumeroLieu()).setCoordinate(Coordinate.newInstance(cc.getCoordX(), cc.getCoordY())).build())
                    .setServiceTime(cc.getDureeService())
                    .build();
            ls.add(s);
        }
        //define a matrix-builder building a symmetric matrix
        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(false);
        for(CommandeClient cc : ccc){
            for(CommandeClient cc2 : ccc){
                if(cc == cc2)continue;
                Trajet t = JpaTrajetDao.getInstance().find(cc, cc2);
                costMatrixBuilder.addTransportDistance(cc.getNumeroLieu(), cc2.getNumeroLieu(), t.getDistance());
                costMatrixBuilder.addTransportTime(cc.getNumeroLieu(), cc2.getNumeroLieu(), t.getDuree());
            }
            
            /**
             * Client -> Depot
             */
            Trajet t_d = JpaTrajetDao.getInstance().find(cc, d);
            costMatrixBuilder.addTransportDistance(cc.getNumeroLieu(), d.getNumeroLieu(), t_d.getDistance());
            costMatrixBuilder.addTransportTime(cc.getNumeroLieu(), d.getNumeroLieu(), t_d.getDuree());
            
            /**
             * Depot -> Client
             */
            t_d = JpaTrajetDao.getInstance().find(d, cc);
            costMatrixBuilder.addTransportDistance(d.getNumeroLieu(), cc.getNumeroLieu(), t_d.getDistance());
            costMatrixBuilder.addTransportTime(d.getNumeroLieu(), cc.getNumeroLieu(), t_d.getDuree());
        }
        
        VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
        
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.setRoutingCost(costMatrix);
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addAllJobs(ls);
        
        VehicleRoutingProblem problem = vrpBuilder.build();

		/*
         * get the algorithm out-of-the-box.
		 */
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

		/*
         * and search a solution
		 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
         * get the best
		 */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
        
        Collection<VehicleRoute> cvr = bestSolution.getRoutes();
        
        

		/*
         * plot
		 */
        //new Plotter(problem,bestSolution).plot("output/plot.png","simple example");
        
        return cvr;

        /*
        render problem and solution with GraphStream
         */
        //new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(200).display();
        
    }
    
    public static void makeSolution(Collection<VehicleRoute> cvr){
        //On creer un Vehicule action tournant
        VehiculeAction va;
        
        //On creer un nouveau Véhicule
        Vehicule v = new Vehicule();;
        
        Depot d = Algo.getDepot();
        
        for(VehicleRoute vr : cvr){
            
            // On instancie un nouveau Véhicule
            v = new Vehicule();
           
            for(TourActivity ta : vr.getActivities()){
                
                // On instancie un nouveau VéhiculeAction
                va= new VehiculeAction();
                CommandeClient l = (CommandeClient) JpaLieuDao.getInstance().findLieuByNumeroLieu(ta.getLocation().getId());
                
                if(v.getActions().size() == 0){
                    
                    va.setDepart(d);
                    
                    va.setArrivee(l);
                    Trajet t = JpaTrajetDao.getInstance().find(d, l);
                    va.setDistance(t.getDistance());
                    va.setDuree(t.getDuree());
                    va.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
                    va.setIsTrain(false);
                    va.setVehicule(v);
                    
                    // On instancie un nouveau VéhiculeAction
                    va= new VehiculeAction();
                    
                    va.setDepart(l);
                    va.setArrivee(l);
                    va.setDistance(0);
                    va.setDuree(l.getDureeService());
                    va.setIsTrain(false);
                    va.setEnumAction(VehiculeAction.EnumAction.TRAITEMENT);
                    va.setVehicule(v);
                }
                
                else {
                    
                    // On instancie un nouveau VéhiculeAction
                    va= new VehiculeAction();
                    
                    CommandeClient prev_l = (CommandeClient) JpaLieuDao.getInstance().findLieuByNumeroLieu(vr.getActivities().get(vr.getActivities().indexOf(ta) - 1).getLocation().getId());
                    va.setDepart(prev_l);
                    va.setArrivee(l);
                    Trajet t = JpaTrajetDao.getInstance().find(prev_l, l);
                    va.setDuree(t.getDuree());
                    va.setDistance(t.getDistance());
                    va.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
                    va.setIsTrain(false);
                    va.setVehicule(v);
                    
                    // On instancie un nouveau VéhiculeAction
                    va= new VehiculeAction();
                    
                    va.setDepart(l);
                    va.setArrivee(l);
                    va.setDistance(0);
                    va.setDuree(l.getDureeService());
                    va.setEnumAction(VehiculeAction.EnumAction.TRAITEMENT);
                    va.setIsTrain(false);
                    va.setVehicule(v);
                    
                    
                }
                
                if(vr.getActivities().indexOf(ta) == vr.getActivities().size() -1){
                    
                    // On instancie un nouveau VéhiculeAction
                    va= new VehiculeAction();
                    
                    va.setDepart(l);
                    va.setArrivee(d);
                    Trajet t = JpaTrajetDao.getInstance().find(l, d);
                    va.setDuree(t.getDuree());
                    va.setDistance(t.getDistance());
                    va.setEnumAction(VehiculeAction.EnumAction.DEPLACEMENT);
                    va.setIsTrain(false);
                    va.setVehicule(v);
                    
                    
                }
                
                //On assigne la commande au véhicule
                v.add(l);
                
                //on genere le colis
                Colis c = new Colis();
                c.setCommande(l);
                c.setQuantite(l.getQuantiteVoulue());
                c.setSwapBody(v.getSwapBodies().get(0));
                
                
            }
            
            JpaVehiculeDao.getInstance().create(v);
            
            
        }
        
        
    }
    
    
    
    public static void main(String[] args) {
        Depot dp = Algo.getDepot();
        Constantes.coutTrajetCamion = (float)0.5;
        Constantes.coutCamion = 100;
        Constantes.coutDureeCamion = 20;
        Constantes.dureeMaxTournee = 28800;
        Collection<CommandeClient> cccc = JpaCommandeClientDao.getInstance().findAllCamions();
        makeSolution(calc(cccc, dp));
    }
}
