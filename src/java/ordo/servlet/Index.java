/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.SwapBody;
import ordo.data.entities.Vehicule;

/**
 *
 * @author Nicolas
 */
public class Index extends HttpServlet {

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
        
        JpaVehiculeDao daoVehicule = JpaVehiculeDao.getInstance();
        //Collection<Vehicule> vehicules = daoVehicule.findAll();
        Collection<Vehicule> vehicules = new ArrayList<>();
        
        Vehicule v1 = new Vehicule();
        Vehicule v2 = new Vehicule();
        v1.setId(1);
        v2.setId(2);
        v2.addSwapBody(new SwapBody());
        v2.addSwapBody(new SwapBody());
        
        v1.setDistanceParcourue(300000);
        v1.setTempsTrajet(300);
        v2.setDistanceParcourue(240000);
        v2.setTempsTrajet(260);
        
        
        CommandeClient cc1 = new CommandeClient();
        CommandeClient cc2 = new CommandeClient();
        CommandeClient cc3 = new CommandeClient();
        cc1.setId(1);
        cc2.setId(2);
        cc3.setId(3);
        cc1.setLibelle("Sopra-Steria");
        cc2.setLibelle("Boulanger");
        cc3.setLibelle("Engie");
        
        v1.add(cc1);
        v1.add(cc2);
        v2.add(cc3);
        
        vehicules.add(v1);
        vehicules.add(v2);
        
        request.setAttribute("vehicules", vehicules);
        
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
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
