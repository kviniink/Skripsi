/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import twitter4j.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public final class TwitterGateway implements StatusListener{
    private String user;
    private String location[];
    private String latlon[] = new String[2];
    private RoutingResponse routingResponse;
    private Step[] step;
    private Steps steps;
    public static final String screenName = "@KvinIink";
    
    public void onStatus(Status status) {
        System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
        user = status.getUser().getScreenName();
        String mentionStatus = status.getText();
        location = mentionStatus.split(" to ");
        if(location.length == 2){
            try {           
                location[0] = location[0].substring(screenName.length() + 1); //ditambah 2 buat karakter @ sama space
                //string destination nampung hasil dari JSONObject hasil pencarian apa ajah yang ketemu dari KIRIGateway.getLatLong
                String destination1 = KIRIGateway.GetLatLong(location[0]);
                String destination2 = KIRIGateway.GetLatLong(location[1]);

                //dimasukin ke JSONObject
                JSONObject objDest1 = new JSONObject(destination1);
                JSONObject objDest2 = new JSONObject(destination2);
                
                //masukin hasil pencarian pertama dari JSONObject ke routinResponse
                JSONObject res1 = objDest1.getJSONArray("searchresult").getJSONObject(0);
                String hasilDest1 = res1.getString("placename");
                latlon[0] = res1.getString("location");

                JSONObject res2 = objDest2.getJSONArray("searchresult").getJSONObject(0);
                String hasilDest2 = res2.getString("placename");
                latlon[1] = res2.getString("location");

                //dapet hasilPencarian terus dimasukin ke JSONArray paramSteps buat dipisah-pisah trus dimasukin RoutingResponse
                String hasilPencarian = KIRIGateway.GetTrack(latlon[0], latlon[1]);
                JSONObject objTrack = new JSONObject(hasilPencarian);
                JSONObject routingresults = objTrack.getJSONArray("routingresults").getJSONObject(0);
                JSONArray paramSteps = routingresults.getJSONArray("steps");

                //bikin variable step, steps, dan routing response
                step = new Step[paramSteps.length()];
                for (int i = 0; i < step.length; i++) {
                    step[i] = new Step(paramSteps.getJSONArray(i).getString(3) + "");
                }
        
                steps = new Steps(step);
                routingResponse = new RoutingResponse(objTrack.getString("status"), steps);
                if(routingResponse.getStatus().equals("ok")){
                    for (int i = 0; i < routingResponse.getRoutingResult().getSteps().length ; i++) {
                        tweetMoreThan140(user, routingResponse.getRoutingResult().getSteps()[i].getHumanDescription());
                    }
                }else{
                    System.out.println("status error");
                }
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(TwitterGateway.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Format yang anda masukkan salah");
            //tweet format salah (lokasi coma boleh 2)
        }
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    public void onException(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStallWarning(StallWarning sw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void tweet(String user, String paramStatusUpdate){
        Twitter twitter = new TwitterFactory().getInstance();
        StatusUpdate statusUpdate = new StatusUpdate("@"+user + " " + paramStatusUpdate);
        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException ex) {
            System.out.println("Error : " + ex);
        }
    }
    
    public void tweetMoreThan140(String user, String paramStatusUpdate){
        Twitter twitter = new TwitterFactory().getInstance();
        int lengthStatusUpdate = user.length() + paramStatusUpdate.length() + 2;
//        System.out.println(paramStatusUpdate);
        
        String[] tampung = paramStatusUpdate.split(" ", 0);
        
        StatusUpdate[] statusUpdate = new StatusUpdate[(lengthStatusUpdate / 140) + 1];
        String[] tampungStatusUpdate = new String[statusUpdate.length];
//        System.out.println(statusUpdate.length + " : status Update length");
//        System.out.println(tampungStatusUpdate.length + " : tampung status Update length");
        int increment = 0;
        int incTampung = 0;
        for (int i = 0; i < tampungStatusUpdate.length; i++) {
            tampungStatusUpdate[i] = "@" + user + " ";
//            System.out.println("tampung Status Update " + i+ " " + tampungStatusUpdate[i]);
        }
        
        while(increment < statusUpdate.length){
//            System.out.println("increment : " + increment);
//            System.out.println("status update : " + statusUpdate.length);
            
            while(incTampung < tampung.length){
                tampungStatusUpdate[increment] += tampung[incTampung];
//                System.out.println(tampungStatusUpdate[increment] + " | Increment : "+increment+" | length : "+tampungStatusUpdate[increment].length() + " tampung: " + tampung[incTampung].length());
                if(tampungStatusUpdate[increment].length() >= 140){
//                    System.out.println(tampungStatusUpdate[increment] + " | Increment : "+increment+" | length : "+tampungStatusUpdate[increment].length() + " tampung: " + tampung[incTampung].length());
                    
                    tampungStatusUpdate[increment] = tampungStatusUpdate[increment].substring(0, tampungStatusUpdate[increment].length() - tampung[incTampung].length());
//                    System.out.println(tampungStatusUpdate[increment] + " | Increment : "+increment+" | length : "+tampungStatusUpdate[increment].length() + " tampung: " + tampung[incTampung].length());
                    
                    increment++;
//                    System.out.println(increment + "inc");
                    
                    tampungStatusUpdate[increment] += tampung[incTampung] + " ";
//                    System.out.println(tampungStatusUpdate[increment] + " | Increment : "+increment+" | length : "+tampungStatusUpdate[increment].length() + " tampung: " + tampung[incTampung].length());
                    break;
                }else{
                    tampungStatusUpdate[increment] += " ";
//                    System.out.println(increment + "else inc");
                }
                incTampung++;
                if(incTampung >= tampung.length){
                    increment++;
                }
            }
        }
        
        for (int i = 0; i < statusUpdate.length; i++) {
            statusUpdate[i] = new StatusUpdate(tampungStatusUpdate[i]);
        }
//        for (int i = 0; i < statusUpdate.length; i++) {
//            System.out.println(statusUpdate[i]);
//        }
        try {
            for (int i = 0; i < statusUpdate.length; i++) {
                twitter.updateStatus(statusUpdate[i]);
            }
        } catch (TwitterException ex) {
            System.out.println("Error : " + ex);
        }
    }
}