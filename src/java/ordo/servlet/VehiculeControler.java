/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Vehicule;
import ordo.data.entities.VehiculeAction;

/**
 *
 * @author Olivier
 */
public class VehiculeControler extends HttpServlet {

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
        // On get la dao
        JpaVehiculeDao daoVehicule = JpaVehiculeDao.getInstance();
        // On get le vehicule
        Vehicule v = daoVehicule.find(Long.parseLong(request.getParameter("id")));
        // On set le vehicule dans la requete
        request.setAttribute("clients", v.getCommandes());
        List<VehiculeAction> vas = v.getActions();
        Collections.sort(vas, new Comparator<VehiculeAction>(){
            @Override
            public int compare(VehiculeAction o1, VehiculeAction o2) {
                return (int) (o1.getId() - o2.getId());
            }
        });
        request.setAttribute("vas", v.getActions());
        
        // On récupère les Colis du swapBody 1 
        request.setAttribute("colis", v.getSwapBodies().get(0).getColis());
        if(v.getSwapBodies().size() > 1)
            request.setAttribute("colis2", v.getSwapBodies().get(1).getColis());
        else
            request.setAttribute("colis2", null);
        
        //On forward
        getServletContext().getRequestDispatcher("/vehicule.jsp").forward(request, response);
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
