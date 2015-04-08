/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Kevin
 */
public class RoutingResponse {
    public String status;
    public Steps routingResult;

    public RoutingResponse(String paramStatus, Steps paramRoutingResult){
        this.status = paramStatus;
        this.routingResult = paramRoutingResult;
    }

    public RoutingResponse() {
        this.status = "";
        this.routingResult = null;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Steps getRoutingResult() {
        return routingResult;
    }

    public void setRoutingResult(Steps routingResult) {
        this.routingResult = routingResult;
    }

    
    
}
