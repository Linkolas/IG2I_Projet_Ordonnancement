/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.metier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaDepotDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.entities.Depot;
import ordo.data.entities.SwapLocation;
import ordo.data.entities.CommandeClient;

/**
 *
 * @author Axelle
 */
public class CSVReader
{
    //Index fleet
    private static final int fleet_index_type = 0;
    private static final int fleet_index_capacity = 1;
    private static final int fleet_index_costsKm = 2;
    private static final int fleet_index_costsHour = 3;
    private static final int fleet_index_costsUsage = 4;
    private static final int fleet_index_operatingTime = 5;
    
    //Index locations
    private static final int locations_index_type = 0;
    private static final int locations_index_id = 1;
    private static final int locations_index_postCode = 2;
    private static final int locations_index_city = 3;
    private static final int locations_index_Xcoord = 4;
    private static final int locations_index_Ycoord = 5;
    private static final int locations_index_quantity = 6;
    private static final int locations_index_trainPossible = 7;
    private static final int locations_index_serviceTime = 8;
    
    //Index SwapActions
    private static final int swapActions_index_action = 0;
    private static final int swapActions_index_duration = 1;
    
    //Lists
    private List fleet = new ArrayList();
    private List location = new ArrayList();
    private List swapActions = new ArrayList();

    public CSVReader()
    {
    }
    
    public void readAllCSV()
    {
        //readFleet();
        readLocations();
        //readSwapActions();
    }
    
    public void readFleet()
    {
        BufferedReader fileReader = null;
        String currentLine = "";
        try
        {
            String fileName = System.getProperty("user.home")+"/Desktop/projet2017/large_normal/Fleet.csv";
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            while ((currentLine = fileReader.readLine()) != null)
            {
                String[] splitedLine = currentLine.split(";");
                System.out.println(splitedLine[0] + " " + splitedLine[1] + " " + splitedLine[2] + " " + splitedLine[3] + " " + splitedLine[4] + " " + splitedLine[5]);
            }
        } 
        
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        finally
        {
            try
            {
                fileReader.close();
            } catch (IOException ex)
            {
                Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void readLocations()
    {
        BufferedReader fileReader = null;
        String currentLine = "";
        
        JpaDepotDao daoDepot = JpaDepotDao.getInstance();
        JpaSwapLocationDao daoSwapLocation = JpaSwapLocationDao.getInstance();
        JpaCommandeClientDao daoClient = JpaCommandeClientDao.getInstance();
        
        
        try
        {
            String fileName = System.getProperty("user.home")+"/Desktop/projet2017/large_normal/Locations.csv";
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            while ((currentLine = fileReader.readLine()) != null)
            {
                String[] splitedLine = currentLine.split(";");
                System.out.println(splitedLine[0] + " " + splitedLine[1] + " " + splitedLine[2] + " " + splitedLine[3] + " " + splitedLine[4] + " " + splitedLine[5] + " " + splitedLine[6] + " " + splitedLine[7] + " " + splitedLine[8]);
                if(splitedLine[0].equals("DEPOT"))
                {
                    Depot d = new Depot();
                    d.setNumeroLieu(splitedLine[locations_index_id]);
                    d.setCodePostal(splitedLine[locations_index_postCode]);
                    d.setVille(splitedLine[locations_index_city]);
                    d.setCoordX(Float.parseFloat(splitedLine[locations_index_Xcoord]));
                    d.setCoordY(Float.parseFloat(splitedLine[locations_index_Ycoord]));
                    
                    daoDepot.create(d);                   
                }
                else if(splitedLine[0].equals("SWAP_LOCATION"))
                {
                    SwapLocation sl = new SwapLocation();
                    sl.setNumeroLieu(splitedLine[locations_index_id]);
                    sl.setCodePostal(splitedLine[locations_index_postCode]);
                    sl.setVille(splitedLine[locations_index_city]);
                    sl.setCoordX(Float.parseFloat(splitedLine[locations_index_Xcoord]));
                    sl.setCoordY(Float.parseFloat(splitedLine[locations_index_Ycoord]));
                    
                    daoSwapLocation.create(sl);
                }
                else
                {
                    CommandeClient c = new CommandeClient();
                    c.setNumeroLieu(splitedLine[locations_index_id]);
                    c.setCodePostal(splitedLine[locations_index_postCode]);
                    c.setVille(splitedLine[locations_index_city]);
                    c.setCoordX(Float.parseFloat(splitedLine[locations_index_Xcoord]));
                    c.setCoordY(Float.parseFloat(splitedLine[locations_index_Ycoord]));
                    c.setQuantiteVoulue(Float.parseFloat(splitedLine[locations_index_quantity]));
                    if(splitedLine[locations_index_trainPossible].equals("1"))
                    {
                        c.setNombreRemorquesMax(2);
                    }
                    c.setDureeService(Float.parseFloat(splitedLine[locations_index_serviceTime]));
                    
                    daoClient.create(c);
                }
            }
        } 
        
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        finally
        {
            try
            {
                fileReader.close();
            } catch (IOException ex)
            {
                Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void readSwapActions()
    {
        BufferedReader fileReader = null;
        String currentLine = "";
        try
        {
            String fileName = System.getProperty("user.home")+"/Desktop/projet2017/large_normal/SwapActions.csv";
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            while ((currentLine = fileReader.readLine()) != null)
            {
                String[] splitedLine = currentLine.split(";");
                System.out.println(splitedLine[0] + " " + splitedLine[1]);
            }
        } 
        
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        finally
        {
            try
            {
                fileReader.close();
            } catch (IOException ex)
            {
                Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args)
    {
        CSVReader csvReader = new CSVReader();
        csvReader.readAllCSV();
    }
}
