/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ordo.cplex.CplexSolve;
import ordo.cplex.CplexTournee;
import ordo.cplex.DeCplexifier;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.entities.CommandeClient;
import ordo.data.metier.CSVReader;

/**
 *
 * @author Nicolas
 */
public class MultiThreadTestController extends HttpServlet {
    
    
    public static final int NUMBER_OF_THREADS = 4;
    public static final int GENERATE_TOURNEES_TIME = 10; // seconds
    public static final int CPLEX_SOLVE_TIME = 60; // seconds
    
    public static final int CPLEX_AUTOTUNE_TIME = 0; // seconds
    
    

    private static final String UPLOAD_DIR = "assets" + File.separator + "csv";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR + File.separator;

        System.out.println("STEP 1 / READING FLEET.CSV");
        CSVReader reader = new CSVReader();
        reader.readFleet(uploadFilePath + "Fleet.csv");
        
        
        CplexSolve cp = new CplexSolve();
        System.out.println("STEP 2 / GENERATING TOURNEES");
        MultiThreadTests mtt = new MultiThreadTests();
        mtt.generateTime = GENERATE_TOURNEES_TIME;
        mtt.threads = NUMBER_OF_THREADS;

        MTTGResults mtresults = mtt.runTests();

        cp = new CplexSolve();
        System.out.println("STEP 3 / SOLVING CPLEX");
        for(CplexTournee ct: mtresults.getTournees()) {
            cp.addTournee(ct);
        }
        cp.setTimeLimit(CPLEX_SOLVE_TIME);
        cp.setEmphasis(CplexSolve.MIPEmphasis.OPTIMALITY);
        //cp.setCutParams();
        if(CPLEX_AUTOTUNE_TIME > 0) {
            cp.setAutoTune(true, CPLEX_AUTOTUNE_TIME);
        }
        //cp.setSolution(findBest(mtresults.getSolutions()));
        cp.solve();
        
        ArrayList<CplexTournee> results = cp.getResults();
        System.out.println("Results found : " + results.size());
        
        System.out.println("STEP 4 / SAVING RESULTS");
        DeCplexifier dec = new DeCplexifier();
        dec.CplexTourneesToSolution(results);
        
        System.out.println("STEP FINAL / DONE");
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MultiThreadTestController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MultiThreadTestController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private MTSolution findBest(Collection<MTSolution> solutions) {
        System.out.println("Searching for potential best Solution...");
        
        
        JpaCommandeClientDao daoCc = JpaCommandeClientDao.getInstance();
        Collection<CommandeClient> ccs      = daoCc.findAll(true);
        
        
        MTSolution bestSolution = null;
        float bestCost = -1;
        for(MTSolution sol : solutions) {
            
            if(!sol.isValid(ccs.size())) {
                System.out.println("Invalid solution");
                continue;
            }
            
            if(sol.getCost() < bestCost || bestCost == -1) {
                bestCost = sol.getCost();
                bestSolution = sol;
            }
        }
        
        System.out.println("Best solution cost : " + bestCost);
        return bestSolution;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
