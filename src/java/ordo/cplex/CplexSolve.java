/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.SwapLocation;

/**
 *
 * @author Nicolas
 */
public class CplexSolve {
    
    public static void main(String[] args) {
        List<CplexTournee> tournees = generateTournees();
        
        try {
            IloCplex cplex = new IloCplex();
            
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<CplexTournee> generateTournees() {
        List<CplexTournee> tournees = new ArrayList<>();
        
        Depot d = new Depot();
        SwapLocation sl = new SwapLocation();
        CommandeClient c1 = new CommandeClient();
        CommandeClient c2 = new CommandeClient();
        
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
        t2.setCost(15);
        t3.setCost(16);
        t4.setCost(19);
        
        tournees.add(t1);
        tournees.add(t2);
        tournees.add(t3);
        tournees.add(t4);
        
        return tournees;
    }
    
    /**
     * Un premier exemple d'utilisation du solveur cplex sur un modele lineaire
     * avec 2 variables et 3 contraintes.
     * Solution optimale :
     * x = 2
     * y = 6
     * obj = 36
     */
    public static void myFirstExample() {
        try {
            IloCplex cplex = new IloCplex();
            cplex.setParam(IloCplex.DoubleParam.TiLim, 300);
            
            // 2 variables de décision : x et y
            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");
            IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");

            // Fonction objectif : max 3x + 5y
            IloLinearNumExpr obj = cplex.linearNumExpr();
            obj.addTerm(x, 3);
            obj.addTerm(y, 5);
            IloObjective objectiveFunction = cplex.addMaximize(obj);

            // Premiere contrainte : x <= 4
            IloRange constraint1 = cplex.addLe(x, 4);
            // Deuxieme contrainte : y <= 6
            IloRange constraint2 = cplex.addLe(y, 6);
            // Troisieme contrainte : 3x + 2y <= 18
            IloLinearNumExpr expr = cplex.linearNumExpr();
            expr.addTerm(x, 3);
            expr.addTerm(y, 2);
            IloRange constraint3 = cplex.addLe(expr, 18);
            
            // Exprot du modele dans un fichier texte
            cplex.exportModel("1stModel.lp");
            
            // Resolution
            cplex.solve();
            
            System.out.println("obj : "+cplex.getObjValue());
            System.out.println("x : "+cplex.getValue(x));
            System.out.println("y : "+cplex.getValue(y));
        } catch (IloException ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
