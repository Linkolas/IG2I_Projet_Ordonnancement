/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.metier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Axelle
 */
public class CSVWriter
{
    public CSVWriter()
    {
    }
    
    public void WriteCSV()
    {
        try
        {
            //On génère le fichier nommé Solution.csv
            FileWriter filewriter = null;           
            filewriter = new FileWriter("Solution.csv");
            
            //On inscrit le header qui respecte le format demandé
            filewriter.append("TOUR_ID;TOUR_POSITION;LOCATION_ID;LOCATION_TYPE;SEMI_TRAILER_ATTACHED;SWAP_BODY_TRUCK;SWAP_BODY_SEMI_TRAILER;SWAP_ACTION;SWAP_BODY_1_QUANTITY;SWAP_BODY_2_QUANTITY");
            //Puis on passe à la ligne
            filewriter.append("\n");
            
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
