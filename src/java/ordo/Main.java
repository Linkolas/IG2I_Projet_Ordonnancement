package ordo;

import ordo.data.dao.JpaLieuDao;
import ordo.data.entities.Lieu;

public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("WORKS");
        
        JpaLieuDao daoLieu = JpaLieuDao.getInstance();
        daoLieu.findAll();
        
    }
}
