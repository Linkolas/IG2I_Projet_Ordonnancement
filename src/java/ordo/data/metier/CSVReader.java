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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaDepotDao;
import ordo.data.dao.jpa.JpaLieuDao;
import ordo.data.dao.jpa.JpaSwapLocationDao;
import ordo.data.entities.Depot;
import ordo.data.entities.SwapLocation;
import ordo.data.entities.CommandeClient;
import ordo.data.Constantes;
import ordo.data.dao.jpa.JpaTrajetDao;
import ordo.data.entities.Lieu;
import ordo.data.entities.Trajet;

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
    
    //Index trajets
    private static final int trajet_index_distance = 0;
    private static final int trajet_index_duree = 1;
    
    //Index coordonnées
    private static final int lieu_index_Xcoord = 0;
    private static final int lieu_index_Ycoord = 1;
    
    //Lists
    //private List fleet = new ArrayList();
    //private List location = new ArrayList();
    //private List swapActions = new ArrayList();

    public CSVReader()
    {
    }
    
    public void readAllCSV()
    {
        readFleet();
        readLocations();
        readSwapActions();
        readTrajets();
    }
    
    public void readFleet()
    {
        BufferedReader fileReader = null;
        String currentLine = "";
        try
        {
            //String fileName = System.getProperty("user.home")+"/Desktop/projet2017/large_normal/Fleet.csv";
            String fileName = "web/assets/csv/Fleet.csv";
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            while ((currentLine = fileReader.readLine()) != null)
            {
                String[] splitedLine = currentLine.split(";");
                
                if(splitedLine[fleet_index_type].equals("TRUCK"))
                {
                    Constantes.coutCamion = Float.parseFloat(splitedLine[fleet_index_costsUsage]);
                    Constantes.coutDureeCamion = Float.parseFloat(splitedLine[fleet_index_costsHour]);
                    Constantes.coutTrajetCamion = Float.parseFloat(splitedLine[fleet_index_costsKm]);
                    
                }
                else if(splitedLine[fleet_index_type].equals("SEMI_TRAILER"))
                {
                    Constantes.coutSecondeRemorque = Float.parseFloat(splitedLine[fleet_index_costsUsage]);
                    Constantes.coutTrajetSecondeRemorque = Float.parseFloat(splitedLine[fleet_index_costsKm]);
                }
                else
                {
                    Constantes.capaciteMax = Float.parseFloat(splitedLine[fleet_index_capacity]);
                    Constantes.dureeMaxTournee = Float.parseFloat(splitedLine[fleet_index_operatingTime]);
                }
            }
            
            /*System.out.println(Constantes.coutCamion);
            System.out.println(Constantes.coutDureeCamion);
            System.out.println(Constantes.coutTrajetCamion);
            System.out.println(Constantes.coutSecondeRemorque);
            System.out.println(Constantes.coutTrajetSecondeRemorque);
            System.out.println(Constantes.capaciteMax);
            System.out.println(Constantes.dureeMaxTournee);*/
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
        String fileName = "web/assets/csv/Locations.csv";
        
        JpaDepotDao daoDepot = JpaDepotDao.getInstance();
        JpaSwapLocationDao daoSwapLocation = JpaSwapLocationDao.getInstance();
        JpaCommandeClientDao daoClient = JpaCommandeClientDao.getInstance();
        
        
        try
        {
            
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            while ((currentLine = fileReader.readLine()) != null)
            {
                String[] splitedLine = currentLine.split(";");
                //System.out.println(splitedLine[0] + " " + splitedLine[1] + " " + splitedLine[2] + " " + splitedLine[3] + " " + splitedLine[4] + " " + splitedLine[5] + " " + splitedLine[6] + " " + splitedLine[7] + " " + splitedLine[8]);
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
        String fileName = "web/assets/csv/SwapActions.csv";
        try
        {
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            while ((currentLine = fileReader.readLine()) != null)
            {
                String[] splitedLine = currentLine.split(";");
                System.out.println(splitedLine[0] + " " + splitedLine[1]);
                if(splitedLine[swapActions_index_action].equals("PARK"))
                {
                    Constantes.dureePark = Float.parseFloat(splitedLine[swapActions_index_duration]);
                }
                else if(splitedLine[swapActions_index_action].equals("SWAP"))
                {
                    Constantes.dureeSwap = Float.parseFloat(splitedLine[swapActions_index_duration]);
                }
                else if(splitedLine[swapActions_index_action].equals("EXCHANGE"))
                {
                    Constantes.dureeExchange = Float.parseFloat(splitedLine[swapActions_index_duration]);
                }
                else
                {
                    Constantes.dureePickup = Float.parseFloat(splitedLine[swapActions_index_duration]);
                }
                
                System.out.println(Constantes.dureePark);
                System.out.println(Constantes.dureeSwap);
                System.out.println(Constantes.dureeExchange);
                System.out.println(Constantes.dureePickup);
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
   
    /**
     * Cette fonction permet de lire les fichiers DistanceTimesData 
     * et d'ajouter les trajets en base
     */
    public void readTrajets()
    {
        // Variables
        //  lieux comprendra les différents lieux qui correspondent aux coordonnees dans la variable coordonnees
        //  coordX sera utile pour les boucles de lecture des CSV
        //  coordY sera utile pour les boucles de lecture des CSV
        List<Lieu> lieux = new ArrayList<>();
        float coordX;
        float coordY;
        
        String file_path_coordinates = "web/assets/csv/DistanceTimesCoordinates.csv";
        String file_path_data = "web/assets/csv/DistanceTimesData.csv";
        
        //On lit le premier fichier CSV : DistanceTimesCoordinates
        BufferedReader fileReader = null;
        String currentLine;
        try
        {
            // Ouverture du fichier
            fileReader = new BufferedReader(new FileReader(file_path_coordinates));
            
            //On lit la première ligne qui comprend les headers
            fileReader.readLine();
            
            // On lit la première ligne du fichier DistanceTimesCoordinates (premier triangle en haut à droite)
            while ((currentLine = fileReader.readLine()) != null)
            {
                // On lit une ligne
                // Récupération des coordonnées en X et en Y
                String[] splitedLine = currentLine.split(";");
                coordX = Float.parseFloat(splitedLine[lieu_index_Xcoord]);
                coordY = Float.parseFloat(splitedLine[lieu_index_Ycoord]);
                
                // On trouve le lieu en base relié à ces coordonnees
                JpaLieuDao daoLieu = JpaLieuDao.getInstance();
                lieux.add(daoLieu.findLieuByCoordonnees(coordX, coordY));
            }
            
            System.out.println(lieux);
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
        
        // On lit le fichier DistanceTimesData
        try
        {
            // nombreLignes correspond au nombre de lignes du fichier DistanceTimesData
            int nombreLignes = lieux.size();
            
            JpaTrajetDao jpaTrajetDao = JpaTrajetDao.getInstance();            
            Trajet trajet;
            
            // trajets est la liste des trajets qu'on ajoutera en base
            List<Trajet> trajets = new ArrayList<>();
            int i=0, j;
            
            // On ouvre le fichier DistanceTimesData
            fileReader = new BufferedReader(new FileReader(file_path_data));
            
            // On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            
            // On récupère les trajets dans le fichier DistanceTimesData
            // D'abord, on récupère la première moitié en haut à droite du fichier
            while ((currentLine = fileReader.readLine()) != null)
            {
                // On lit une ligne (relié à un lieu de départ)
                String[] splitedLine = currentLine.split(";");
                for(j=((i+1)*2); j<(nombreLignes*2); j+=2)
                {
                    // On lit le lieu d'arrivée
                    // On créé le trajet en fonction du CSV
                    trajet = new Trajet();
                    trajet.setDepart(lieux.get(i));
                    trajet.setDistance(Integer.parseInt(splitedLine[j + trajet_index_distance]));
                    trajet.setDuree(Integer.parseInt(splitedLine[j + trajet_index_duree]));
                    trajet.setDestination(lieux.get(j/2));
                    trajets.add(trajet);
                }
                i++;
                System.out.println(i);
            }
            
            fileReader.close();
            
            // On lit la deuxième moitié du fichier DistanceTimesData
            fileReader = new BufferedReader(new FileReader(file_path_data));
            
            // On lit l'entête, que l'on connait déjà
            fileReader.readLine();
            i=0;
            
            // On récupère les trajets dans le fichier DistanceTimesData
            // Maintenant, on récupère la deuxième moitié en bas à gauche
            while ((currentLine = fileReader.readLine()) != null)
            {
                // On lit une ligne (relié à un lieu de départ)
                String[] splitedLine = currentLine.split(";");
                for(j=0; j<=i; j++)
                {
                    // On lit le lieu d'arrivée
                    // On créé le trajet en fonction du CSV
                    trajet = new Trajet();
                    trajet.setDepart(lieux.get(i));
                    trajet.setDistance(Integer.parseInt(splitedLine[(j*2) + trajet_index_distance]));
                    trajet.setDuree(Integer.parseInt(splitedLine[(j*2) + trajet_index_duree]));
                    trajet.setDestination(lieux.get(j));
                    trajets.add(trajet);
                }
                i++;
                System.out.println(i);
            }
            
            // On enregistre tous les trajets en base d'un seul coup
            jpaTrajetDao.create(trajets);
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
