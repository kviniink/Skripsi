
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kevin
 */
public class Main {
    public static void main(String[] args){
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        TwitterGateway twittergateway = new TwitterGateway();
        
        FilterQuery fq = new FilterQuery();
        String keywords[] = {twittergateway.screenName};

        fq.track(keywords);

        twitterStream.addListener(twittergateway);
        twitterStream.filter(fq);
//        twittergateway.Tweet("KvinIink", " Revolver Espresso is one of the coolest coffee joints in Seminyak, Bali. If you love coffee, and enjoy cafe-hopping, you have to drop by Revolver for a cuppa when you are exploring Jalan Laksmana. One will be hard-pressed to find Revolver Espresso unless they are in the know, or stumble on it entirely by accident. The easiest way is to look out for Bali Clinic as they are tucked in a lane opposite the clinic. Alternatively, you can enter from the back of Home Store or This is a Love store.");
    }
}
