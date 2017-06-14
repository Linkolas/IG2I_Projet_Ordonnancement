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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.SwapLocation;

/**
 *
 * @author Nicolas
 */
public class CplexSolve {
    
    private ArrayList<CplexTournee> tournees = new ArrayList<>();
    private IloCplex cplex;
    private HashMap<Lieu, List<IloNumVar>> constraints = new HashMap();
    
    public CplexSolve() {
        try {
            cplex = new IloCplex();
        } catch (IloException ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addTournee(CplexTournee tournee) {
        try {
            IloNumVar var = cplex.boolVar();
            tournee.setCplexVar(var);
            
            for(Lieu lieu: tournee.getLieux()) {
                if(!(lieu instanceof CommandeClient)) {
                    continue;
                }
                
                List<IloNumVar> lieuConstraints = constraints.get(lieu);
                if(lieuConstraints == null) {
                    lieuConstraints = new ArrayList<>();
                }
                lieuConstraints.add(var);
                constraints.put(lieu, lieuConstraints);
            }
            
            tournees.add(tournee);
            
        } catch (IloException ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void solve() {
        try {
            IloLinearNumExpr objective = cplex.linearNumExpr();
            for(CplexTournee ct: tournees) {
                objective.addTerm(ct.getCplexVar(), ct.getCost());
            }
            cplex.addMinimize(objective);
            
            Collection<List<IloNumVar>> listConstraints = constraints.values();
            for(List<IloNumVar> listVars: listConstraints) {
                IloLinearNumExpr contr = cplex.linearNumExpr();
                for(IloNumVar var: listVars) {
                    contr.addTerm(var, 1);
                }
                cplex.addEq(contr, 1);
            }
            
            
            cplex.exportModel("cplex_model.lp");
            
            if(cplex.solve()) {
                System.out.println("");
                System.out.println("Solution status: " + cplex.getStatus());
                System.out.println("\tCost = " + cplex.getObjValue()); 
            }
            
        } catch (IloException ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
            
    public static void main(String[] args) {
        List<CplexTournee> tournees = generateTournees();
        CplexSolve cp = new CplexSolve();
        for(CplexTournee ct: tournees) {
            cp.addTournee(ct);
        }
        cp.solve();
    }
    
    public static void tests() {
        List<CplexTournee> tournees = generateTournees();
        
        try {
            IloCplex cplex = new IloCplex();
            
            generateEquationsV2(cplex);
            
            cplex.exportModel("file_name.lp");
            
            if(cplex.solve()) {
                System.out.println("");
                System.out.println("Solution status: " + cplex.getStatus());
                System.out.println("\tCost = " + cplex.getObjValue()); 
            }
            
        } catch (Exception ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void generateEquationsV1(IloCplex cplex) {
/*
  x1 * (c1 * a + 10 * COST)
+ x2 * (c2 * b + 12 * COST)
+ x3 * (c1 * c + c2 * d + 18 * COST)
+ x4 * (c2 * e + c1 * f + 16 * COST)

a + c + f = 1
b + d + e = 1

min(COST)
*/
        try {
            IloNumVar cost = cplex.numVar(0, Double.MAX_VALUE, "cost");
            IloNumVar a = cplex.boolVar();
            IloNumVar b = cplex.boolVar();
            IloNumVar c = cplex.boolVar();
            IloNumVar d = cplex.boolVar();
            IloNumVar e = cplex.boolVar();
            IloNumVar f = cplex.boolVar();
            
            
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            
            // Cas de test
            IloLinearNumExpr expr1 = cplex.linearNumExpr();
            expr1.addTerm(a, 1);
            expr1.addTerm(cost, 10);
            
            IloLinearNumExpr expr2 = cplex.linearNumExpr();
            expr2.addTerm(b, 1);
            expr2.addTerm(cost, 12);
            
            IloLinearNumExpr expr3 = cplex.linearNumExpr();
            expr3.addTerm(c, 1);
            expr3.addTerm(d, 1);
            expr3.addTerm(cost, 18);
            
            IloLinearNumExpr expr4 = cplex.linearNumExpr();
            expr4.addTerm(e, 1);
            expr4.addTerm(f, 1);
            expr4.addTerm(cost, 16);
            
            objExpr.add(expr1);
            objExpr.add(expr2);
            objExpr.add(expr3);
            objExpr.add(expr4);
            
            
            // CONTRAINTES
            IloLinearNumExpr contr1 = cplex.linearNumExpr();
            contr1.addTerm(a, 1);
            contr1.addTerm(c, 1);
            contr1.addTerm(f, 1);
            
            IloLinearNumExpr contr2 = cplex.linearNumExpr();
            contr2.addTerm(b, 1);
            contr2.addTerm(d, 1);
            contr2.addTerm(e, 1);
            
            cplex.addEq(contr1, 1);
            cplex.addEq(contr2, 1);
            
            
            // OBJECTIF
            IloObjective objective = cplex.addMinimize(objExpr);
        } catch (Exception ex) {
            Logger.getLogger(CplexSolve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void generateEquationsV2(IloCplex cplex) {
/*
    10 * a
+   12 * b
+   18 * c
+   16 * d

a + c + d = 1
b + c + d = 1

min ()
*/
        try {
            // Cas de test
            IloNumVar a = cplex.boolVar();
            IloNumVar b = cplex.boolVar();
            IloNumVar c = cplex.boolVar();
            IloNumVar d = cplex.boolVar();
            
            IloLinearNumExpr expr = cplex.linearNumExpr();
            expr.addTerm(a, 10);
            expr.addTerm(b, 12);
            expr.addTerm(c, 18);
            expr.addTerm(d, 16);
            
            // OBJECTIF
            cplex.addMinimize(expr);
            
            // CONTRAINTES
            IloLinearNumExpr contr1 = cplex.linearNumExpr();
            IloLinearNumExpr contr2 = cplex.linearNumExpr();
            
            contr1.addTerm(a, 1);
            contr1.addTerm(c, 1);
            contr1.addTerm(d, 1);
            
            contr2.addTerm(b, 1);
            contr2.addTerm(c, 1);
            contr2.addTerm(d, 1);
            
            cplex.addEq(contr1, 1);
            cplex.addEq(contr2, 1);
            
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
        d.setId(0);
        sl.setId(1);
        c1.setId(2);
        c2.setId(3);
        
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
