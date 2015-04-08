/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Kevin
 */
class Step {
    public String humanDescription;

    public Step(String paramHumanDescription){
        this.humanDescription = paramHumanDescription;
    }

    public Step() {
        this.humanDescription = "";
    }
    public String getHumanDescription() {
        return humanDescription;
    }

    public void setHumanDescription(String humanDescription) {
        this.humanDescription = humanDescription;
    }
}
