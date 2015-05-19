/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

public final class TwitterGateway implements StatusListener{
    public static final String screenName = "@KvinIink";
    private String user;
    private String location[];
    private String latlon[] = new String[2];
    private RoutingResponse routingResponse;
    private Step[] step;
    private Steps steps;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    
    @Override
    public void onStatus(Status status) {
        user = status.getUser().getScreenName();
        String mentionStatus = status.getText();
        System.out.println("@" + user + " - " + mentionStatus);
        String paramScreenName = screenName.toLowerCase();
        mentionStatus = mentionStatus.toLowerCase().replace(paramScreenName, "");
        location = mentionStatus.split(" to ");
        boolean statusLocation1 = false;
        boolean statusLocation2 = false;
        
        
        if(location.length == 2){
            try {
                System.out.println("Lokasi 1 : "+location[0].trim());
                System.out.println("Lokasi 2 : "+location[1].trim());
                //string destination menampung hasil dari JSONObject hasil pencarian apa saja yang ditemukan dari KIRIGateway.getLatLong
                String destination1 = KIRIGateway.GetLatLong(location[0]);
                String destination2 = KIRIGateway.GetLatLong(location[1]);
                
                //dimasukan ke JSONObject 
                JSONObject objDest1 = new JSONObject(destination1);
                JSONObject objDest2 = new JSONObject(destination2);
                
		//memasukan hasil pencarian pertama dari JSONObject ke abribut routingResponse
                JSONObject res1 = objDest1.getJSONArray("searchresult").getJSONObject(0);
                String hasilDest1 = res1.getString("placename");
                latlon[0] = res1.getString("location");
                if(hasilDest1 != null)
                {
                    statusLocation1 = true;
                }
                
                JSONObject res2 = objDest2.getJSONArray("searchresult").getJSONObject(0);
                String hasilDest2 = res2.getString("placename");
                latlon[1] = res2.getString("location");
                if(hasilDest2 != null)
                {
                    statusLocation2 = true;
                }
                
                //Mendapatkan hasil pencarian lalu dimasukan ke JSONArray paramSteps untuk dipisah-pisah lalu dimasukan ke RoutingResponse
                String hasilPencarian = KIRIGateway.GetTrack(latlon[0], latlon[1]);
                JSONObject objTrack = new JSONObject(hasilPencarian);
                JSONObject routingresults = objTrack.getJSONArray("routingresults").getJSONObject(0);
                JSONArray paramSteps = routingresults.getJSONArray("steps");
                //buat variable step, steps, dan routing response
                step = new Step[paramSteps.length()];
                for (int i = 0; i < step.length; i++) {
                    step[i] = new Step(paramSteps.getJSONArray(i).getString(3) + "");
                }
                steps = new Steps(step);
                
                routingResponse = new RoutingResponse(objTrack.getString("status"), steps);
                if(routingResponse.getStatus().equals("ok")){
                    for (int i = 0; i < routingResponse.getRoutingResult().getSteps().length ; i++) {
                        date = new Date();
                        Tweet(user, routingResponse.getRoutingResult().getSteps()[i].getHumanDescription());
                    }
                    Tweet(user, "Untuk lebih lengkap silahkan lihat di http://kiri.travel?start=" + location[0].replace(" ", "%20") + "&finish="+ location[1].replace(" ", "%20") + "&region=bdo");
                    
                    for (int i = 0; i < routingResponse.getRoutingResult().getSteps().length ; i++) {
                        System.out.println("@"+user + " " + routingResponse.getRoutingResult().getSteps()[i].getHumanDescription());
                    }
                    System.out.println("@"+user + " Untuk lebih lengkap silahkan lihat di http://kiri.travel?start=" + location[0].replace(" ", "%20") + "&finish="+ location[1].replace(" ", "%20") + "&region=bdo");
                }else{
                    System.out.println("status error");
                }
            } catch (Exception ex) {
                try {
                    if(!statusLocation1)
                    {
                        date = new Date();
                        Tweet(user,location[0] + " tidak ditemukan");
                        System.out.println("@"+user+" "+location[0] + " tidak ditemukan");
                    }
                    else if(!statusLocation2)
                    {
                        date = new Date();
                        Tweet(user,location[1] + " tidak ditemukan");
                        System.out.println("@"+user+" "+location[1] + " tidak ditemukan");
                    }
                    else
                    {
                        Tweet(user,"Pencarian tidak ditemukan");
                        System.out.println("@"+user+" Pencarian tidak ditemukan");
                    }
                    //java.util.logging.Logger.getLogger(TwitterGateway.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TwitterException ex1) {
                    java.util.logging.Logger.getLogger(TwitterGateway.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onException(Exception ex) {
        System.out.println("onException");
        ex.printStackTrace();
    }

    @Override
    public void onStallWarning(StallWarning sw) {
        System.out.println("onStallWarning");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void Tweet(String user, String paramStatusUpdate) throws TwitterException
    {
        Twitter twitter = new TwitterFactory().getInstance();
        int userLength = user.length();
        int maxTweet = 140 - (userLength + 2 + 9); // ditambah 2 untuk @ dan " ", ditambah 9 untuk waktu " HH:mm:ss";
        
        String[] tampung = paramStatusUpdate.split(" ", 0); //misahin semua kata jadi array of kata
        
        StatusUpdate[] statusUpdate = new StatusUpdate[(paramStatusUpdate.length() / maxTweet) + 1]; //panjang status dibagi batas maksimal huruf yang diperbolehkan, ditambah 1 karena 130/140 = 0
        String[] tampungStatusUpdate = new String[statusUpdate.length]; //bikin banyaknya array sepanjang tweet yang mau di tweet
        
        int increment = 0;
        int incTampung = 0;
        for (int i = 0; i < tampungStatusUpdate.length; i++) {
            tampungStatusUpdate[i] = "@" + user + " "; //setiap tweet harus dimasukin nama user yang dituju
        }
        
        while(increment < statusUpdate.length){
            
            while(incTampung < tampung.length){
                tampungStatusUpdate[increment] += tampung[incTampung]; //nambahin kata
                if(tampungStatusUpdate[increment].length() >= 131){ //melakukan cek apakah tweet sudah over 140 kata atau belom, 131 soalnya 140 dikurang 9 untuk waktu " HH:mm:ss";
                    
                    tampungStatusUpdate[increment] = tampungStatusUpdate[increment].substring(0, tampungStatusUpdate[increment].length() - tampung[incTampung].length()); //ngebuang kata terakhir yang ditambahin
                    tampungStatusUpdate[increment] += " "+ dateFormat.format(date);
                    increment++;
                    
                    //tampungStatusUpdate[increment] += tampung[incTampung] + " "; //kata yang tadi dibuang dimasukin lagi ke tampungStatusUpdate index selanjutnya
                    break;
                }else{
                    tampungStatusUpdate[increment] += " "; //Jika belum 140 kata maka ditambahkan spasi
                }
                incTampung++;
                if(incTampung >= tampung.length){
                    tampungStatusUpdate[increment] += " "+ dateFormat.format(date);
                    increment++; //kalo kata-kata sudah habis, keluarin dari statement
                }
            }
        }
        
        for (int i = 0; i < statusUpdate.length; i++) {
            statusUpdate[i] = new StatusUpdate(tampungStatusUpdate[i]);
        }
        try {
            for (int i = 0; i < statusUpdate.length; i++) {
                twitter.updateStatus(statusUpdate[i]);
            }
        } catch (TwitterException ex) {
            twitter.updateStatus("@" + user + " Maaf anda sudah penah melakukan pencarian ini sebelumnya. " + dateFormat.format(date) );
            System.out.println("Error : " + ex);
        }
    }
}