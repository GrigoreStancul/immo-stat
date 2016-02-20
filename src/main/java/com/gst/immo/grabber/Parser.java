package com.gst.immo.grabber;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.opencsv.CSVWriter;


public class Parser implements Closeable {

   private static final String WWW_IMMOBILIENSCOUT24_DE = "www.immobilienscout24.de";
   public static Logger        _log                     = Logger.getLogger(Parser.class);
   private CSVWriter           _csvWriter;

   public void init() throws IOException {
      File targetDir = new File("logs");
      if ( !targetDir.exists() ) {
         targetDir.mkdirs();
      }
      _csvWriter = new CSVWriter(new FileWriter(targetDir.getAbsolutePath() + "\\results.csv"), '\t');
      _csvWriter.writeNext(
         new String[] { "id", "name", "urlStr", "price", "rooms", "wohnflaeche", "grundstueck", "haustyp", "hausgeld", "baujahr", "address", "breadCrumb" });
   }

   @Override
   public void close() throws IOException {
      _csvWriter.close();
   }

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

   private void parseExposePage( String urlStr ) throws IOException {
      Document doc = Jsoup.connect(urlStr).get();

      String id = getSimpleFieldData(doc, "ul.is24-ex-id");
      String name = getSimpleFieldData(doc, "h1#expose-title");
      String price = getSimpleFieldData(doc, "div.is24qa-kaufpreis");
      String rooms = getSimpleFieldData(doc, "div.is24qa-zi");
      String wohnflaeche = getSimpleFieldData(doc, Arrays.asList("div.is24qa-wohnflaeche", "dd.is24qa-wohnflaeche-ca"));
      String grundstueck = getSimpleFieldData(doc, "div.is24qa-grundstueck");
      String haustyp = getSimpleFieldData(doc, Arrays.asList("dd.is24qa-haustyp", "dd.is24qa-wohnungstyp"));
      String hausgeld = getSimpleFieldData(doc, "dd.is24qa-hausgeld");
      String baujahr = getSimpleFieldData(doc, "dd.is24qa-baujahr");
      String address = getSimpleFieldData(doc, "div.address-block");
      String breadCrumb = getBreadCrumb(doc);

      wohnflaeche = proc_wohnflaeche_checkMsquared(wohnflaeche);
      address = proc_address(address);
      _csvWriter.writeNext(new String[] { id, name, urlStr, price, rooms, wohnflaeche, grundstueck, haustyp, hausgeld, baujahr, address, breadCrumb });

      //      System.out.println("ID: " + id);
      //      System.out.println("Price: " + price);
      //      System.out.println("Rooms: " + rooms);
      //      System.out.println("Wohnflaeche: " + wohnflaeche);
      //      System.out.println("Grundstueck: " + grundstueck);
      //      System.out.println("Haustyp: " + haustyp);
      //      System.out.println("Address: " + address);
      //      System.out.println("Hausgeld: " + hausgeld);
      //      System.out.println("Baujahr: " + baujahr);
   }

   private String getBreadCrumb( Document doc ) {
      Elements elements = doc.select("div#is24-main");
      return elements.first().child(0).child(0).text();
   }

   private String proc_address( String address ) {
      String sf = "Die vollständige Adresse der Immobilie erhalten Sie vom Anbieter.";
      if ( address.contains(sf) ) {
         address = address.replace(sf, "");
      }
      return address;
   }

   private String proc_wohnflaeche_checkMsquared( String wohnflaeche ) {
      if ( wohnflaeche.contains(" m²") ) {
         wohnflaeche = wohnflaeche.replace(" m²", "");
      }
      return wohnflaeche;
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

   private List<String> extractPageInformation( String urlStr ) throws IOException {
      List<String> result = new ArrayList<String>();
      Document doc = Jsoup.connect(urlStr).get();
      Elements elements = doc.select("div#pageSelection select option");
      elements.forEach(e -> result.add(buildSearchUrlFromPageLink(e.attr("value"))));
      return result;
   }

   private void processQuery( String urlStr ) throws IOException {
      List<String> pageInformation = extractPageInformation(urlStr);
      if ( pageInformation.isEmpty() ) {
         parseQueryPage(urlStr);
      } else {
         for ( String queryPage : pageInformation ) {
            parseQueryPage(queryPage);
         }
      }

   }

   private void parseQueryPage( String urlStr ) throws IOException {
      //      loadPage(urlStr);
      Document doc = Jsoup.connect(urlStr).get();
      Elements elements = doc.select("ul#resultListItems article div.result-list-entry__data a");
      List<String> links = new ArrayList<String>();
      elements.forEach(e -> links.add(e.attr("href")));

      Set<Long> exposeIds = new HashSet<Long>();
      links.forEach(link -> exposeIds.add(parseExposeLinkId(link)));

      for ( Long exposeId : exposeIds ) {
         if ( exposeId != null ) {
            processExpose(exposeId);
         }
      }
   }

   private void processExpose( long exposeId ) throws IOException {
      _log.info("Start parsing exposeId: " + exposeId);
      String exposeUrl = buildExposeUrl(exposeId);
      parseExposePage(exposeUrl);
   }

   private static Long parseExposeLinkId( String link ) {
      String prefix = "/expose/";
      if ( link.startsWith(prefix) ) {
         link = link.substring(prefix.length());
         try {
            Long l = Long.parseLong(link);
            return l;
         }
         catch ( NumberFormatException nfe ) {}
      }
      return null;
   }

   private static String buildSearchUrlFromPageLink( String pageLink ) {
      return "http://" + WWW_IMMOBILIENSCOUT24_DE + pageLink;
   }

   private static String buildExposeUrl( long exposeId ) {
      return "http://" + WWW_IMMOBILIENSCOUT24_DE + "/expose/" + exposeId;
   }

   public static void main( String[] args ) throws IOException {
      Parser grabber = new Parser();
      grabber.init();
      //      String urlStr = "http://www.immobilienscout24.de/expose/87092546?PID=56199115&ftc=9004EXPXXUA&utm_medium=email&utm_source=system&utm_campaign=default_fulfillment&utm_content=default_expose&CCWID=$CWID_CONTACT$";
      //      String urlStr = "http://www.immobilienscout24.de/expose/81752382";
      String urlStr = "http://www.immobilienscout24.de/Suche/S-T/Wohnung-Kauf/Baden-Wuerttemberg/Karlsruhe/Daxlanden";
      // grabber.loadPage(urlStr);
      grabber.processQuery(urlStr);
      grabber.close();
   }


}
