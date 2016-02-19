package com.gst.immo.grabber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Grabber {
   
   private void loadPage(String urlStr) throws IOException{
      URL url = new URL(urlStr);
      URLConnection connection = url.openConnection();

      //Spoof the connection so we look like a web browser
      connection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)" );
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String strLine = "";
      String finalHTML = "";
      //Loop through every line in the source
      while ((strLine = in.readLine()) != null){
         finalHTML += strLine + "\n";
      } 
      System.out.println(finalHTML);
   }

   public static void main( String[] args ) throws IOException {
      Grabber grabber = new Grabber();
      String urlStr = "http://www.immobilienscout24.de/expose/87092546?PID=56199115&ftc=9004EXPXXUA&utm_medium=email&utm_source=system&utm_campaign=default_fulfillment&utm_content=default_expose&CCWID=$CWID_CONTACT$";
      grabber.loadPage(urlStr);
   }

}
