/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.metier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordo.data.dao.jpa.JpaSolutionDao;
import ordo.data.dao.jpa.JpaVehiculeActionDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.Vehicule;
import ordo.data.entities.VehiculeAction;

/**
 *
 * @author Axelle
 */
public class CSVWriter
{
    private static final String DELIMITEUR = ";";
    private static final String SEPARATEUR_LIGNE = "\n";
    
    public CSVWriter()
    {
    }
    
    public void WriteCSV()
    {
        try
        {
            JpaVehiculeDao jpaVehiculeDao = JpaVehiculeDao.getInstance();
            JpaVehiculeActionDao jpaVehiculeActionDao = JpaVehiculeActionDao.getInstance();
            
            //On génère le fichier nommé Solution.csv
            FileWriter filewriter = null;           
            filewriter = new FileWriter("Solution.csv");
            
            //On inscrit le header qui respecte le format demandé
            filewriter.append("TOUR_ID;TOUR_POSITION;LOCATION_ID;LOCATION_TYPE;SEMI_TRAILER_ATTACHED;SWAP_BODY_TRUCK;SWAP_BODY_SEMI_TRAILER;SWAP_ACTION;SWAP_BODY_1_QUANTITY;SWAP_BODY_2_QUANTITY");
            //Puis on passe à la ligne
            filewriter.append(SEPARATEUR_LIGNE);
            
                
            List<Vehicule> vehicules = (List<Vehicule>) jpaVehiculeDao.findAll();
            List<VehiculeAction> vehiculeActions;
            String TOUR_ID, 
                    TOUR_POSITION, 
                    LOCATION_ID, 
                    LOCATION_TYPE, 
                    SEMI_TRAILER_ATTACHED, 
                    SWAP_BODY_TRUCK, 
                    SWAP_BODY_SEMI_TRAILLER,
                    SWAP_ACTION,
                    SWAP_BODY_1_QUANTITY,
                    SWAP_BODY_2_QUANTITY;
            
            Integer i = 1;
            for(Vehicule vehicule: vehicules)
            {
                TOUR_ID = 'R'+ i.toString();
                
                vehiculeActions = jpaVehiculeActionDao.findByVehicule(vehicule);
                
                for(VehiculeAction vehiculeAction: vehiculeActions)
                {
                    Integer j=1;
                    TOUR_POSITION = j.toString();
                    
                    if(j == 1)
                    {
                        LOCATION_ID = "D1";
                        LOCATION_TYPE = "DEPOT";
                        if(vehicule.isTrain())
                        {
                            SEMI_TRAILER_ATTACHED = "1";
                            SWAP_BODY_TRUCK = "1";
                            SWAP_BODY_SEMI_TRAILLER = "2";
                        }
                        else
                        {
                            SEMI_TRAILER_ATTACHED = "0";
                            SWAP_BODY_TRUCK = "1";
                            SWAP_BODY_SEMI_TRAILLER = "0";
                        }
                        SWAP_ACTION = "NONE";
                        SWAP_BODY_1_QUANTITY = "0";
                        SWAP_BODY_2_QUANTITY = "0";
                    }
                    else
                    {
                        LOCATION_ID = "";
                        LOCATION_TYPE ="";
                        SEMI_TRAILER_ATTACHED = "";
                        SWAP_BODY_TRUCK = "";
                        SWAP_BODY_SEMI_TRAILLER = "";
                        SWAP_ACTION = "";
                        SWAP_BODY_1_QUANTITY = "";
                        SWAP_BODY_2_QUANTITY = "";
                    }
                    filewriter.append(TOUR_ID);                 filewriter.append(DELIMITEUR);
                    filewriter.append(TOUR_POSITION);           filewriter.append(DELIMITEUR);
                    filewriter.append(LOCATION_ID);             filewriter.append(DELIMITEUR);
                    filewriter.append(LOCATION_TYPE);           filewriter.append(DELIMITEUR);
                    filewriter.append(SEMI_TRAILER_ATTACHED);   filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_TRUCK);         filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_SEMI_TRAILLER); filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_ACTION);             filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_1_QUANTITY);    filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_2_QUANTITY);    filewriter.append(DELIMITEUR);
                    
                    filewriter.append(SEPARATEUR_LIGNE);
                    j++;
                }
                i++;
            }
            
            //TODO : récupérer la solution et ses informations liées 
            
            
            //On flush puis on ferme 
            filewriter.flush();
            filewriter.close();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args)
    {
        CSVWriter csvWriter = new CSVWriter();
        csvWriter.WriteCSV();
    }
}
