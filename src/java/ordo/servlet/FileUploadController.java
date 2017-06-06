/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import ordo.data.metier.CSVReader;

/**
 *
 * @author Nicolas
 */
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB 
                 maxFileSize=1024*1024*50,      	// 50 MB
                 maxRequestSize=1024*1024*100,          // 100 MB
                 location = "")   	
public class FileUploadController extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet FileUploadedController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet FileUploadedController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        response.sendRedirect("index");
    }

    /**
     * Directory where uploaded files will be saved, its relative to
     * the web application directory.
     */
    private static final String UPLOAD_DIR = "assets" + File.separator + "csv";
     
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
        
        File temp = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        System.out.println(temp.getPath());
        
        // gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(uploadFilePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        
        String fileName = "";
        //Get all the parts from request and write it to the file on server
        for (Part part : request.getParts()) {
            fileName = getFileName(part);
            //part.write(uploadFilePath + File.separator + fileName);
            part.write(fileName);
        }
 
        // Move the file to its expected location
        File file = new File(temp.getAbsolutePath() + File.separator + fileName);
        file.renameTo(new File(uploadFilePath + File.separator + fileName));
        
        
        CSVReader csvReader = new CSVReader();
        switch(fileName) {
            case "Fleet.csv":
                csvReader.readFleet(uploadFilePath + File.separator + fileName);
                break;
            case "Locations.csv":
                csvReader.readLocations(uploadFilePath + File.separator + fileName);
                break;
            case "SwapActions.csv":
                csvReader.readSwapActions(uploadFilePath + File.separator + fileName);
                break;
            case "DistanceTimesData.csv":
                break;
            case "DistanceTimesCoordinates.csv":
                break;
            default:
                break;
        }
        
        
        request.setAttribute("message", fileName + " File uploaded successfully!");
        getServletContext().getRequestDispatcher("/index.jsp").forward(
                request, response);
        
        
        //response.sendRedirect("index");
    }
 
    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                
                String rtn = token.substring(token.indexOf("=") + 2, token.length()-1);
                if(rtn.startsWith("\"")) {
                    rtn = rtn.substring(1);
                }
                if(rtn.endsWith("\"")) {
                    rtn = rtn.substring(0, rtn.length());
                }
                
                return rtn;
            }
        }
        return "";
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
