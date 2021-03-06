/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.servlet.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ordo.algo.Algo;
import static ordo.algo.AlgoRandom.makeTourneesRandom;
import ordo.algo.HypoTournee;
import ordo.cplex.CplexSolve;
import ordo.cplex.CplexTournee;
import ordo.cplex.DeCplexifier;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.entities.CommandeClient;
import ordo.data.metier.CSVReader;
import ordo.data.metier.CSVWriter;
import static ordo.jsprit.RunWithoutSwapLocation.calc;
import static ordo.jsprit.RunWithoutSwapLocation.makeSolution;

/**
 *
 * @author Nicolas
 */
@WebServlet(name = "APICalculateJspritController", urlPatterns = {"/api/calculatejsprit"})
public class APICalculateJspritController extends HttpServlet {

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
        
        int generateTourneesDuringSeconds = 30;
        int cplexSolveLimitSeconds = generateTourneesDuringSeconds;
        
        System.out.println("STEP 1 / READING FLEET.CSV");
        CSVReader reader = new CSVReader();
        reader.readFleet(uploadFilePath + "Fleet.csv");
        
        
        System.out.println("STEP 3 / FIND BEST SOLUTION");
        Collection<CommandeClient> cccc = JpaCommandeClientDao.getInstance().findAllCamions();
        
        makeSolution(calc(cccc, Algo.getDepot()));
        
        System.out.println("STEP 4 / WRITING SOLUTION.CSV");
        CSVWriter writer = new CSVWriter();
        writer.WriteCSV();
        
        String json = "{\"success\": true}";
        PrintWriter out = response.getWriter();
        out.print(json);
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
