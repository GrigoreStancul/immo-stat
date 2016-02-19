package com.gst.immo.grabber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Grabber {

   public static Logger _log = Logger.getLogger(Grabber.class);

   private void loadPage( String urlStr ) throws IOException {
      URL url = new URL(urlStr);
      URLConnection connection = url.openConnection();

      //Spoof the connection so we look like a web browser
      connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)");
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String strLine = "";
      String finalHTML = "";
      //Loop through every line in the source
      while ( (strLine = in.readLine()) != null ) {
         finalHTML += strLine + "\n";
      }
      System.out.println(finalHTML);
   }

   private void parsePage( String urlStr ) throws IOException {
      Document doc = Jsoup.connect(urlStr).get();

      String id = getSimpleFieldData(doc, "ul.is24-ex-id");
      String price = getSimpleFieldData(doc, "div.is24qa-kaufpreis");
      String rooms = getSimpleFieldData(doc, "div.is24qa-zi");
      String wohnflaeche = getSimpleFieldData(doc, Arrays.asList("div.is24qa-wohnflaeche", "dd.is24qa-wohnflaeche-ca"));
      String grundstueck = getSimpleFieldData(doc, "div.is24qa-grundstueck");
      String haustyp = getSimpleFieldData(doc, Arrays.asList("dd.is24qa-haustyp", "dd.is24qa-wohnungstyp"));
      String hausgeld = getSimpleFieldData(doc, "dd.is24qa-hausgeld");
      String baujahr = getSimpleFieldData(doc, "dd.is24qa-baujahr");
      String address = getSimpleFieldData(doc, "div.address-block");

      //      addressNode.forEach(e -> System.out.println(e));

      System.out.println("ID: " + id);
      System.out.println("Price: " + price);
      System.out.println("Rooms: " + rooms);
      System.out.println("Wohnflaeche: " + wohnflaeche);
      System.out.println("Grundstueck: " + grundstueck);
      System.out.println("Haustyp: " + haustyp);
      System.out.println("Address: " + address);
      System.out.println("Hausgeld: " + hausgeld);
      System.out.println("Baujahr: " + baujahr);
   }

   private String getSimpleFieldData( Document doc, String cssQuery ) {
      String data = null;
      Elements node = doc.select(cssQuery);
      if ( node != null && node.first() != null ) {
         data = node.first().text();
      } else {
         _log.trace(cssQuery + " not found");
      }
      return data;
   }

   private String getSimpleFieldData( Document doc, List<String> cssQuerys ) {
      for ( String cssQuery : cssQuerys ) {
         String simpleFieldData = getSimpleFieldData(doc, cssQuery);
         if ( simpleFieldData != null ) {
            return simpleFieldData;
         }
      }
      return null;
   }


   public static void main( String[] args ) throws IOException {
      Grabber grabber = new Grabber();
      //      String urlStr = "http://www.immobilienscout24.de/expose/87092546?PID=56199115&ftc=9004EXPXXUA&utm_medium=email&utm_source=system&utm_campaign=default_fulfillment&utm_content=default_expose&CCWID=$CWID_CONTACT$";
      String urlStr = "http://www.immobilienscout24.de/expose/81752382";
      // grabber.loadPage(urlStr);
      grabber.parsePage(urlStr);
   }

}
