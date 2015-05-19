/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author Kevin
 */
public class KIRIGateway {
    public static String GetLatLong(String destination) throws Exception {
            String url = "http://kiri.travel/handle.php?version=2&mode=searchplace&region=bdo&querystring=" + URLEncoder.encode(destination.trim(), "UTF-8")+"&apikey=889C2C8FBB82C7E6";
            
            //System.out.println(url);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();

            return response.toString();
	}
        
        public static String GetTrack(String dest1, String dest2) throws Exception {
            //presentation == desktop biar tidak ada %toicon and %fromicon
            String url = "http://kiri.travel/handle.php?version=2&mode=findroute&locale=id&start="+dest1+"&finish="+dest2+"&presentation=desktop&apikey=889C2C8FBB82C7E6";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();

            return response.toString();
	}
}
