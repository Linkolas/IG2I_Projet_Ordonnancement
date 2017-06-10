/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ordo.data.dao.jpa.*;

/**
 *
 * @author Flo
 */
public class DeleteAll extends HttpServlet
{

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
            throws ServletException, IOException
    {
        JpaCommandeClientDao jpaCommandeClientDao = JpaCommandeClientDao.getInstance();
        JpaDepotDao jpaDepotDao = JpaDepotDao.getInstance();
        JpaLieuDao jpaLieuDao = JpaLieuDao.getInstance();
        JpaSolutionDao jpaSolutionDao = JpaSolutionDao.getInstance();
        JpaSwapBodyDao jpaSwapBodyDao = JpaSwapBodyDao.getInstance();
        JpaSwapLocationDao jpaSwapLocationDao = JpaSwapLocationDao.getInstance();
        JpaTrajetDao jpaTrajetDao = JpaTrajetDao.getInstance();
        JpaVehiculeActionDao jpaVehiculeActionDao = JpaVehiculeActionDao.getInstance();
        JpaVehiculeDao jpaVehiculeDao = JpaVehiculeDao.getInstance();
        JpaColisDao jpaColisDao = JpaColisDao.getInstance();

        jpaTrajetDao.deleteAll();
        jpaColisDao.deleteAll();
        jpaVehiculeActionDao.deleteAll();
        jpaCommandeClientDao.deleteAll();
        jpaDepotDao.deleteAll();
        jpaLieuDao.deleteAll();
        jpaSolutionDao.deleteAll();
        jpaSwapBodyDao.deleteAll();
        jpaSwapLocationDao.deleteAll();
        jpaVehiculeDao.deleteAll();
        
        response.sendRedirect("index");
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
            throws ServletException, IOException
    {
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
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
