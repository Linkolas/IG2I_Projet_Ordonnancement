package ordo;

import ordo.data.dao.JpaDepotDao;
import ordo.data.dao.JpaLieuDao;
import ordo.data.entities.Depot;
import ordo.data.entities.Lieu;

public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("WORKS");
        
        Lieu l = new Lieu();
        l.setVille("Lieu");
        JpaLieuDao daoLieu = JpaLieuDao.getInstance();
        daoLieu.create(l);
        daoLieu.findAll();
        
        Depot d = new Depot();
        d.setVille("Depot");
        JpaDepotDao daoDepot = JpaDepotDao.getInstance();
        daoDepot.create(d);
        daoDepot.findAll();
        
    }
}
