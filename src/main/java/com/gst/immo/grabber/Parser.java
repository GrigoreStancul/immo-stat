package com.gst.immo.grabber;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gst.immo.db.ExposeBase;
import com.gst.immo.db.ImmoDAO;
import com.opencsv.CSVWriter;

public class Parser implements Closeable {

	public static Logger _log = Logger.getLogger(Parser.class);
	private CSVWriter _csvWriter;
	private ClassPathXmlApplicationContext _springContext;
	private ImmoDAO _immoDAO;

	public void init() throws IOException {
		File targetDir = new File("logs");
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		_csvWriter = new CSVWriter(new FileWriter(targetDir.getAbsolutePath() + "\\results.csv"), '\t');
		_csvWriter.writeNext(new String[] { "id", "name", "urlStr", "price", "rooms", "wohnflaeche", "grundstueck",
				"haustyp", "hausgeld", "baujahr", "address", "breadCrumb" });

		_springContext = new ClassPathXmlApplicationContext("spring.xml");
		_immoDAO = _springContext.getBean(ImmoDAO.class);
	}

	@Override
	public void close() throws IOException {
		_csvWriter.close();
		_springContext.close();
	}

	private void parseExposePage(ExposeBase exposeBase) throws IOException {
		String exposeUrl = Grabber.buildExposeUrl(exposeBase.getExposeId());
		Document doc = Jsoup.parse(exposeBase.getHtmlPage(), exposeUrl);

		String id = getSimpleFieldData(doc, "ul.is24-ex-id");
		String name = getSimpleFieldData(doc, "h1#expose-title");
		String price = getSimpleFieldData(doc, "div.is24qa-kaufpreis");
		String rooms = getSimpleFieldData(doc, "div.is24qa-zi");
		String wohnflaeche = getSimpleFieldData(doc,
				Arrays.asList("div.is24qa-wohnflaeche", "dd.is24qa-wohnflaeche-ca"));
		String grundstueck = getSimpleFieldData(doc, "div.is24qa-grundstueck");
		String haustyp = getSimpleFieldData(doc, Arrays.asList("dd.is24qa-haustyp", "dd.is24qa-wohnungstyp"));
		String hausgeld = getSimpleFieldData(doc, "dd.is24qa-hausgeld");
		String baujahr = getSimpleFieldData(doc, "dd.is24qa-baujahr");
		String address = getSimpleFieldData(doc, "div.address-block");
		String breadCrumb = getBreadCrumb(doc);

		wohnflaeche = proc_wohnflaeche_checkMsquared(wohnflaeche);
		address = proc_address(address);
		_csvWriter.writeNext(new String[] { id, name, exposeUrl, price, rooms, wohnflaeche, grundstueck, haustyp,
				hausgeld, baujahr, address, breadCrumb });

		// System.out.println("ID: " + id);
		// System.out.println("Price: " + price);
		// System.out.println("Rooms: " + rooms);
		// System.out.println("Wohnflaeche: " + wohnflaeche);
		// System.out.println("Grundstueck: " + grundstueck);
		// System.out.println("Haustyp: " + haustyp);
		// System.out.println("Address: " + address);
		// System.out.println("Hausgeld: " + hausgeld);
		// System.out.println("Baujahr: " + baujahr);
	}

	private String getBreadCrumb(Document doc) {
		Elements elements = doc.select("div#is24-main");
		return elements.first().child(0).child(0).text();
	}

	private String proc_address(String address) {
		String sf = "Die vollständige Adresse der Immobilie erhalten Sie vom Anbieter.";
		if (address.contains(sf)) {
			address = address.replace(sf, "");
		}
		return address;
	}

	private String proc_wohnflaeche_checkMsquared(String wohnflaeche) {
		if (wohnflaeche.contains(" m²")) {
			wohnflaeche = wohnflaeche.replace(" m²", "");
		}
		return wohnflaeche;
	}

	private String getSimpleFieldData(Document doc, String cssQuery) {
		String data = null;
		Elements node = doc.select(cssQuery);
		if (node != null && node.first() != null) {
			data = node.first().text();
		} else {
			_log.trace(cssQuery + " not found");
		}
		return data;
	}

	private String getSimpleFieldData(Document doc, List<String> cssQuerys) {
		for (String cssQuery : cssQuerys) {
			String simpleFieldData = getSimpleFieldData(doc, cssQuery);
			if (simpleFieldData != null) {
				return simpleFieldData;
			}
		}
		return null;
	}

	private void processDatabaseData() throws IOException {
		ExposeBase exposeBase = _immoDAO.getExposeBaseDAO().get(33140150);
		parseExposePage(exposeBase);
	}

	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
		parser.init();
		try {
			parser.processDatabaseData();
		} finally {
			parser.close();
		}
	}

}
