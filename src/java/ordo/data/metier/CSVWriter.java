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
            // On récupère les DAO
            JpaVehiculeDao jpaVehiculeDao = JpaVehiculeDao.getInstance();
            JpaVehiculeActionDao jpaVehiculeActionDao = JpaVehiculeActionDao.getInstance();
            JpaCommandeClientDao jpaCommandeClientDao = JpaCommandeClientDao.getInstance();
            
            //On génère le fichier nommé Solution.csv, situé à la racine du projet
            FileWriter filewriter = null;           
            filewriter = new FileWriter("Solution.csv");
            
            //On inscrit le header qui respecte le format demandé
            filewriter.append("TOUR_ID;TOUR_POSITION;LOCATION_ID;LOCATION_TYPE;SEMI_TRAILER_ATTACHED;SWAP_BODY_TRUCK;SWAP_BODY_SEMI_TRAILER;SWAP_ACTION;SWAP_BODY_1_QUANTITY;SWAP_BODY_2_QUANTITY");
            //Puis on passe à la ligne
            filewriter.append(SEPARATEUR_LIGNE);
            
            // On récupère la liste de toutes les tournées
            List<Vehicule> vehicules = (List<Vehicule>) jpaVehiculeDao.findAll();
            List<VehiculeAction> vehiculeActions;
            
            // Création de toutes les variables qui seront les valeurs du CSV
            String TOUR_ID = "", 
                    TOUR_POSITION = "", 
                    LOCATION_ID = "", 
                    LOCATION_TYPE = "", 
                    SEMI_TRAILER_ATTACHED = "", 
                    SWAP_BODY_TRUCK = "", 
                    SWAP_BODY_SEMI_TRAILER = "",
                    SWAP_ACTION = "",
                    SWAP_BODY_1_QUANTITY = "",
                    SWAP_BODY_2_QUANTITY = "";
            
            // i est l'itérateur de tournée
            // j est l'itérateur d'actions
            Integer i = 1, j=1;
            Lieu depart, arrivee;
            CommandeClient commande;
            List<Colis> listeColis;
            
            // On parcourt les tournées
            for(Vehicule vehicule: vehicules)
            {
                TOUR_ID = 'R'+ i.toString();
                
                // Récupération de toutes les actions de la tournée, qu'on trie par ordre dans la tournée
                vehiculeActions = jpaVehiculeActionDao.findByVehicule(vehicule);
                Collections.sort(vehiculeActions, new Comparator<VehiculeAction>()
                {
                    @Override
                    public int compare(VehiculeAction action1, VehiculeAction action2)
                    {
                        if(action1.getId() > action2.getId())
                        {
                            return 1;
                        }
                        return -1;
                    }
                });
                
                j=1;
                
                // On parcourt toutes les actions de la tournée
                for(VehiculeAction vehiculeAction: vehiculeActions)
                {
                    TOUR_POSITION = j.toString();
                    
                    // La première ligne concerne le départ du dépot, qui ne figure pas dans la BDD
                    if(j == 1)
                    {
                        LOCATION_ID = "D1";
                        LOCATION_TYPE = "DEPOT";
                        if(vehicule.isTrain())
                        {
                            SEMI_TRAILER_ATTACHED = "1";
                            SWAP_BODY_TRUCK = "1";
                            SWAP_BODY_SEMI_TRAILER = "2";
                        }
                        else
                        {
                            SEMI_TRAILER_ATTACHED = "0";
                            SWAP_BODY_TRUCK = "1";
                            SWAP_BODY_SEMI_TRAILER = "0";
                        }
                        SWAP_ACTION = "NONE";
                        SWAP_BODY_1_QUANTITY = "0";
                        SWAP_BODY_2_QUANTITY = "0";
                    }
                    // Toutes les autres actions sauf le retour au dépôt
                    else
                    {
                        depart = vehiculeAction.getDepart();
                        LOCATION_ID = depart.getNumeroLieu();
                        
                        // On passe par le dépôt
                        if(depart instanceof Depot)
                        {
                            LOCATION_TYPE = "DEPOT";
                        }
                        
                        // On passe chez un client
                        else if(depart instanceof CommandeClient)
                        {
                            LOCATION_TYPE = "CUSTOMER";
                            
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
                                // Récupération de la liste des colis
                                // Afin d'indiquer les quantités livrées par chacun des swap_body
                                SWAP_ACTION = "NONE";
                                commande = jpaCommandeClientDao.find(vehiculeAction.getArrivee().getId());
                                listeColis = commande.getColis();
                                SWAP_BODY_1_QUANTITY = ""+listeColis.get(0).getQuantite();
                                if(listeColis.size()==2)
                                {
                                    SWAP_BODY_2_QUANTITY = ""+listeColis.get(1).getQuantite();   
                                }
                            }
                        }
                        
                        // On passe par une swap_location
                        else
                        {
                            // Ici, on va changer l'ordre des swap_body en fonction de l'action réalisée
                            LOCATION_TYPE = "SWAP_LOCATION";
                            SWAP_ACTION = vehiculeAction.getEnumAction().toString();
                            SWAP_BODY_1_QUANTITY = "0";
                            SWAP_BODY_2_QUANTITY = "0";
                            
                            if(vehiculeAction.getEnumAction() == VehiculeAction.EnumAction.EXCHANGE)
                            {
                                // C'est un exchange, on récupère donc l'autre swap_body
                                // Et on laisse l'autre au swap_location
                                SEMI_TRAILER_ATTACHED = "0";
                                if(SWAP_BODY_TRUCK.equals("1"))
                                {
                                    SWAP_BODY_TRUCK = "2";
                                    SWAP_BODY_SEMI_TRAILER= "1";
                                }
                                else
                                {
                                    SWAP_BODY_TRUCK = "1";
                                    SWAP_BODY_SEMI_TRAILER = "2";
                                }
                            }
                            else if (vehiculeAction.getEnumAction() == VehiculeAction.EnumAction.PARK)
                            {
                                // On gare le deuxième swap_body au swap_location
                                SEMI_TRAILER_ATTACHED = "0";
                            }
                            else if (vehiculeAction.getEnumAction() == VehiculeAction.EnumAction.PICKUP)
                            {
                                // On récupère le deuxième swap_body
                                SEMI_TRAILER_ATTACHED = "1";
                            }
                            else if (vehiculeAction.getEnumAction() == VehiculeAction.EnumAction.SWAP)
                            {
                                // On échange les deux swap_body
                                if(SWAP_BODY_SEMI_TRAILER == "1")
                                {
                                    SWAP_BODY_TRUCK = "1";
                                    SWAP_BODY_SEMI_TRAILER = "2";
                                }
                                else
                                {
                                    SWAP_BODY_SEMI_TRAILER = "1";
                                    SWAP_BODY_TRUCK = "2";
                                }
                            }
                        }
                    }
                    
                    // Ecriture de la ligne sur le CSV
                    filewriter.append(TOUR_ID);                 filewriter.append(DELIMITEUR);
                    filewriter.append(TOUR_POSITION);           filewriter.append(DELIMITEUR);
                    filewriter.append(LOCATION_ID);             filewriter.append(DELIMITEUR);
                    filewriter.append(LOCATION_TYPE);           filewriter.append(DELIMITEUR);
                    filewriter.append(SEMI_TRAILER_ATTACHED);   filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_TRUCK);         filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_SEMI_TRAILER); filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_ACTION);             filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_1_QUANTITY);    filewriter.append(DELIMITEUR);
                    filewriter.append(SWAP_BODY_2_QUANTITY);    filewriter.append(DELIMITEUR);
                    
                    filewriter.append(SEPARATEUR_LIGNE);
                    j++;
                }
                
                // Dernière action réalisée, retour au dépot
                TOUR_POSITION = Integer.toString(j-1);
                LOCATION_ID = "D1";
                LOCATION_TYPE = "DEPOT";
                if(vehicule.isTrain())
                {
                    SEMI_TRAILER_ATTACHED = "1";
                    SWAP_BODY_TRUCK = "1";
                    SWAP_BODY_SEMI_TRAILER = "2";
                }
                else
                {
                    SEMI_TRAILER_ATTACHED = "0";
                    SWAP_BODY_TRUCK = "1";
                    SWAP_BODY_SEMI_TRAILER = "0";
                }
                SWAP_ACTION = "NONE";
                SWAP_BODY_1_QUANTITY = "0";
                SWAP_BODY_2_QUANTITY = "0";
                
                // On écrit cette dernière ligne 
                filewriter.append(TOUR_ID);                 filewriter.append(DELIMITEUR);
                filewriter.append(TOUR_POSITION);           filewriter.append(DELIMITEUR);
                filewriter.append(LOCATION_ID);             filewriter.append(DELIMITEUR);
                filewriter.append(LOCATION_TYPE);           filewriter.append(DELIMITEUR);
                filewriter.append(SEMI_TRAILER_ATTACHED);   filewriter.append(DELIMITEUR);
                filewriter.append(SWAP_BODY_TRUCK);         filewriter.append(DELIMITEUR);
                filewriter.append(SWAP_BODY_SEMI_TRAILER); filewriter.append(DELIMITEUR);
                filewriter.append(SWAP_ACTION);             filewriter.append(DELIMITEUR);
                filewriter.append(SWAP_BODY_1_QUANTITY);    filewriter.append(DELIMITEUR);
                filewriter.append(SWAP_BODY_2_QUANTITY);    filewriter.append(DELIMITEUR);

                filewriter.append(SEPARATEUR_LIGNE);
                
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
    
    public static void main(String[] args)
    {
        CSVWriter csvWriter = new CSVWriter();
        csvWriter.WriteCSV();
    }
}
