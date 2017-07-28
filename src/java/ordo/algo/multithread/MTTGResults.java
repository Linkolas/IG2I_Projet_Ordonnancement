/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordo.algo.multithread;

import java.util.HashSet;
import java.util.Set;
import ordo.algo.HypoTournee;

/**
 *
 * @author Nicolas
 */
public class MTTGResults {
    private Set<MTTournee> tournees = new HashSet<>();
    private Set<MTSolution> solutions = new HashSet<>();
    private String message = "";

    public Set<MTTournee> getTournees() {
        return tournees;
    }

    public void setTournees(Set<MTTournee> tournees) {
        this.tournees = tournees;
    }
    
    public Set<MTSolution> getSolutions() {
        return solutions;
    }

    public void setSolutions(Set<MTSolution> solutions) {
        this.solutions = solutions;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
