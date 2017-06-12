/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.data.metier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordo.data.dao.jpa.JpaCommandeClientDao;
import ordo.data.dao.jpa.JpaSolutionDao;
import ordo.data.dao.jpa.JpaVehiculeActionDao;
import ordo.data.dao.jpa.JpaVehiculeDao;
import ordo.data.entities.Colis;
import ordo.data.entities.CommandeClient;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;
import ordo.data.entities.Vehicule;
import ordo.data.entities.VehiculeAction;
import static ordo.data.entities.VehiculeAction.VehiculeActionIdComparator;

/**
 *
 * @author Axelle
 */
public class CSVWriter
{
    private static final String DELIMITEUR = ";";
    private static final String SEPARATEUR_LIGNE = "\n";
    
    // Index des colonnes sur le CSV créé
    private static final int INDEX_TOUR_ID = 0;
    private static final int INDEX_TOUR_POSITION = 1;
    private static final int INDEX_LOCATION_ID = 2;
    private static final int INDEX_LOCATION_TYPE = 3;
    private static final int INDEX_SEMI_TRAILER_ATTACHED = 4;
    private static final int INDEX_SWAP_BODY_TRUCK = 5;
    private static final int INDEX_SWAP_BODY_SEMI_TRAILER = 6;
    private static final int INDEX_SWAP_ACTION = 7;
    private static final int INDEX_SWAP_BODY_1_QUANTITY = 8;
    private static final int INDEX_SWAP_BODY_2_QUANTITY = 9;
    
    public CSVWriter()
    {
    }
    
    
    
    public void WriteCSV()
    {
        try
        {
            // On récupère les DAO
            JpaVehiculeDao jpaVehiculeDao = JpaVehiculeDao.getInstance();
            JpaVehiculeActionDao jpaVehiculeActionDao = JpaVehiculeActionDao.getInstance();
            
            //On génère le fichier nommé Solution.csv, situé à la racine du projet
            FileWriter filewriter = new FileWriter("Solution.csv");
            
            //On inscrit le header qui respecte le format demandé
            filewriter.append("TOUR_ID;TOUR_POSITION;LOCATION_ID;LOCATION_TYPE;SEMI_TRAILER_ATTACHED;SWAP_BODY_TRUCK;SWAP_BODY_SEMI_TRAILER;SWAP_ACTION;SWAP_BODY_1_QUANTITY;SWAP_BODY_2_QUANTITY");
            filewriter.append(SEPARATEUR_LIGNE);
            
            // On récupère la liste de toutes les tournées
            List<Vehicule> vehicules = (List<Vehicule>) jpaVehiculeDao.findAll();
            List<VehiculeAction> vehiculeActions;
            
            // Création de toutes les variables qui seront les valeurs du CSV
            String valeurs[] = {"","","","","","","","","",""};
            
            // i est l'itérateur de tournée
            // j est l'itérateur d'actions
            Integer i = 1, j;
            Lieu depart;
            
            // On parcourt les tournées
            for(Vehicule vehicule: vehicules)
            {
                valeurs[INDEX_TOUR_ID] = 'R'+ i.toString();
                
                // Récupération de toutes les actions de la tournée, qu'on trie par ordre dans la tournée
                vehiculeActions = jpaVehiculeActionDao.findByVehicule(vehicule);
                Collections.sort(vehiculeActions, VehiculeActionIdComparator);
                
                j=1;
                
                // On parcourt toutes les actions de la tournée
                for(VehiculeAction vehiculeAction: vehiculeActions)
                {
                    valeurs[INDEX_TOUR_POSITION] = j.toString();
                    
                    // La première ligne concerne le départ du dépot, qui ne figure pas dans la BDD
                    if(j == 1)
                    {
                        ecrirePremiereAction(valeurs, vehicule);
                    }
                    // Toutes les autres actions sauf le retour au dépôt
                    else
                    {
                        depart = vehiculeAction.getDepart();
                        valeurs[INDEX_LOCATION_ID] = depart.getNumeroLieu();
                        
                        // On passe par le dépôt
                        if(depart instanceof Depot)
                        {
                            valeurs[INDEX_LOCATION_TYPE] = "DEPOT";
                        }
                        
                        // On passe chez un client
                        else if(depart instanceof CommandeClient)
                        {                            
                            // Dans ce cas, il s'agit d'un déplacement, ce ne doit pas figurer dans le CSV
                            // On continue donc jusqu'à la prochaine itération
                            if(vehiculeAction.getEnumAction() == VehiculeAction.EnumAction.DEPLACEMENT)
                            {
                                j++;
                                continue;
                            }
                            
                            // On livre un client
                            else
                            {
                                ecritureLivraisonClient(valeurs, vehiculeAction);
                            }
                        }
                        
                        // On passe par une swap_location
                        else
                        {                            
                            ecritureSwapActions(valeurs, vehiculeAction);
                        }
                    }
                    
                    ecrireLigne(valeurs, filewriter);
                    j++;
                }
                
                valeurs[INDEX_TOUR_POSITION] = Integer.toString(j-1);
                ecrireDerniereAction(valeurs, vehicule, filewriter);
                
                i++;
            }
            
            //On flush puis on ferme le fichier
            filewriter.flush();
            filewriter.close();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Ecrit une ligne dans le fichier actuellement ouvert
     * @param valeurs Tableau de valeurs qui représentent les valeurs à insérer dans le fichier
     * @param filewriter la variable qui représente le fichier actuellement ouvert
     */
    public void ecrireLigne(String valeurs[], FileWriter filewriter)
    {    
        try
        {
            // Chaque itération de boucle correspond à une cellule
            for(int valeur=0; valeur < valeurs.length; valeur++)
            {
                filewriter.append(valeurs[valeur]);
                if(valeur != valeurs.length-1)
                {
                    filewriter.append(DELIMITEUR);
                }
            }
            filewriter.append(SEPARATEUR_LIGNE);
        } catch (IOException ex)
        {
            Logger.getLogger(CSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ecrirePremiereAction(String valeurs[], Vehicule vehicule)
    {
        valeurs[INDEX_LOCATION_ID] = "D1";
        valeurs[INDEX_LOCATION_TYPE] = "DEPOT";
        if(vehicule.isTrain())
        {
            valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "1";
            valeurs[INDEX_SWAP_BODY_TRUCK] = "1";
            valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "2";
        }
        else
        {
            valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "0";
            valeurs[INDEX_SWAP_BODY_TRUCK] = "1";
            valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "0";
        }
        valeurs[INDEX_SWAP_ACTION] = "NONE";
        valeurs[INDEX_SWAP_BODY_1_QUANTITY] = "0";
        valeurs[INDEX_SWAP_BODY_2_QUANTITY] = "0";
    }
    
    public void ecritureSwapActions(String valeurs[], VehiculeAction vehiculeAction)
    {
        // Ici, on va changer l'ordre des swap_body en fonction de l'action réalisée
        valeurs[INDEX_LOCATION_TYPE] = "SWAP_LOCATION";
        valeurs[INDEX_SWAP_ACTION] = vehiculeAction.getEnumAction().toString();
        valeurs[INDEX_SWAP_BODY_1_QUANTITY] = "0";
        valeurs[INDEX_SWAP_BODY_2_QUANTITY] = "0";
        
        VehiculeAction.EnumAction swapAction = vehiculeAction.getEnumAction();
        
        switch(swapAction)
        {
            case EXCHANGE: 
                // C'est un exchange, on récupère donc l'autre swap_body
                // Et on laisse l'autre au swap_location
                valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "0";
                if(valeurs[INDEX_SWAP_BODY_TRUCK].equals("1"))
                {
                    valeurs[INDEX_SWAP_BODY_TRUCK] = "2";
                    valeurs[INDEX_SWAP_BODY_SEMI_TRAILER]= "1";
                }
                else
                {
                    valeurs[INDEX_SWAP_BODY_TRUCK] = "1";
                    valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "2";
                }
            break;
            
            case PARK:
                // On gare le deuxième swap_body au swap_location
                valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "0";
            break;
            
            case PICKUP:
                // On récupère le deuxième swap_body
                valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "1";
            break;
            
            case SWAP:
                // On échange les deux swap_body
                if("1".equals(valeurs[INDEX_SWAP_BODY_SEMI_TRAILER]))
                {
                    valeurs[INDEX_SWAP_BODY_TRUCK] = "1";
                    valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "2";
                }
                else
                {
                    valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "1";
                    valeurs[INDEX_SWAP_BODY_TRUCK] = "2";
                }
            break;
        }
    }
    
    public void ecritureLivraisonClient(String valeurs[], VehiculeAction vehiculeAction)
    {
        JpaCommandeClientDao jpaCommandeClientDao = JpaCommandeClientDao.getInstance();
        
        // Récupération de la liste des colis
        // Afin d'indiquer les quantités livrées par chacun des swap_body
        valeurs[INDEX_LOCATION_TYPE] = "CUSTOMER";
        valeurs[INDEX_SWAP_ACTION] = "NONE";
        CommandeClient commande = jpaCommandeClientDao.find(vehiculeAction.getArrivee().getId());
        List<Colis> listeColis = commande.getColis();
        valeurs[INDEX_SWAP_BODY_1_QUANTITY] = ""+listeColis.get(0).getQuantite();
        if(listeColis.size()==2)
        {
            valeurs[INDEX_SWAP_BODY_2_QUANTITY] = ""+listeColis.get(1).getQuantite();   
        }
    }
    
    public void ecrireDerniereAction(String valeurs[], Vehicule vehicule, FileWriter filewriter)
    {
        // Dernière action réalisée, retour au dépot
        valeurs[INDEX_LOCATION_ID] = "D1";
        valeurs[INDEX_LOCATION_TYPE] = "DEPOT";
        if(vehicule.isTrain())
        {
            valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "1";
            valeurs[INDEX_SWAP_BODY_TRUCK] = "1";
            valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "2";
        }
        else
        {
            valeurs[INDEX_SEMI_TRAILER_ATTACHED] = "0";
            valeurs[INDEX_SWAP_BODY_TRUCK] = "1";
            valeurs[INDEX_SWAP_BODY_SEMI_TRAILER] = "0";
        }
        valeurs[INDEX_SWAP_ACTION] = "NONE";
        valeurs[INDEX_SWAP_BODY_1_QUANTITY] = "0";
        valeurs[INDEX_SWAP_BODY_2_QUANTITY] = "0";

        ecrireLigne(valeurs, filewriter);
    }
    
    public static void main(String[] args)
    {
        CSVWriter csvWriter = new CSVWriter();
        csvWriter.WriteCSV();
    }
}
