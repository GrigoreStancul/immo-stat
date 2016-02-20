package com.gst.immo.grabber;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gst.immo.db.ExposeBase;
import com.gst.immo.db.ImmoDAO;
import com.gst.immo.db.Query;
import com.gst.immo.db.SubQuery;
import com.gst.immo.db.SubqueryToExpose;

public class Grabber implements Closeable {

	private static final String WWW_IMMOBILIENSCOUT24_DE = "www.immobilienscout24.de";
	public static Logger _log = Logger.getLogger(Grabber.class);
	private ClassPathXmlApplicationContext _springContext;
	private ImmoDAO _immoDAO;
	private int _exposeNumber = 0;

	public static String buildExposeUrl(long exposeId) {
		return "http://" + WWW_IMMOBILIENSCOUT24_DE + "/expose/" + exposeId;
	}

	public void init() throws IOException {
		_springContext = new ClassPathXmlApplicationContext("spring.xml");
		_immoDAO = _springContext.getBean(ImmoDAO.class);
	}

	@Override
	public void close() throws IOException {
		_springContext.close();
	}

	private String loadPage(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		URLConnection connection = url.openConnection();

		// Spoof the connection so we look like a web browser
		connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String strLine = "";
		String finalHTML = "";
		// Loop through every line in the source
		while ((strLine = in.readLine()) != null) {
			finalHTML += strLine + "\n";
		}
		return finalHTML;
	}

	private List<String> extractPageInformation(String urlStr) throws IOException {
		List<String> result = new ArrayList<String>();
		Document doc = Jsoup.connect(urlStr).get();
		Elements elements = doc.select("div#pageSelection select option");
		elements.forEach(e -> result.add(buildSearchUrlFromPageLink(e.attr("value"))));
		return result;
	}

	private void parseQueryPage(Query query, String urlStr) throws IOException {
		SubQuery subQuery = new SubQuery();
		subQuery.setQuery(query);
		subQuery.setSubQuery(urlStr);
		_immoDAO.getSubQueryDAO().persist(subQuery);

		Document doc = Jsoup.connect(urlStr).get();
		Elements elements = doc.select("ul#resultListItems article div.result-list-entry__data a");
		List<String> links = new ArrayList<String>();
		elements.forEach(e -> links.add(e.attr("href")));

		Set<Long> exposeIds = new HashSet<Long>();
		links.forEach(link -> exposeIds.add(parseExposeLinkId(link)));

		for (Long exposeId : exposeIds) {
			if (exposeId != null) {
				processExpose(subQuery, exposeId);
			}
		}
	}

	private void processExpose(SubQuery subQuery, long exposeId) {
		_exposeNumber++;
		_log.info("Start parsing " + _exposeNumber + " expose. ExposeId: " + exposeId);
		ExposeBase exposeBase = _immoDAO.getExposeBaseDAO().get(exposeId);

		String status = "allreadyexists";
		if (exposeBase == null) {
			String exposeUrl = buildExposeUrl(exposeId);
			String loadedPage = null;
			try {
				loadedPage = loadPage(exposeUrl);
				status = "created";
			} catch (IOException e) {
				_log.error("CAn not load page exposeUrl", e);
				status = "error";
			}

			exposeBase = new ExposeBase();
			exposeBase.setExposeId(exposeId);
			exposeBase.setHtmlPage(loadedPage);
			_immoDAO.getExposeBaseDAO().persist(exposeBase);
		}

		SubqueryToExpose subqueryToExpose = new SubqueryToExpose();
		subqueryToExpose.setExposeBase(exposeBase);
		subqueryToExpose.setSubQuery(subQuery);
		subqueryToExpose.setStatus(status);
		_immoDAO.getSubqueryToExposeDAO().persist(subqueryToExpose);
	}

	private static Long parseExposeLinkId(String link) {
		String prefix = "/expose/";
		if (link.startsWith(prefix)) {
			link = link.substring(prefix.length());
			try {
				Long l = Long.parseLong(link);
				return l;
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	private static String buildSearchUrlFromPageLink(String pageLink) {
		return "http://" + WWW_IMMOBILIENSCOUT24_DE + pageLink;
	}

	private void processQuery(String urlStr) throws IOException {
		Query query = new Query();
		query.setQuery(urlStr);
		_immoDAO.getQueryDAO().persist(query);

		List<String> pageInformation = extractPageInformation(urlStr);
		if (pageInformation.isEmpty()) {
			parseQueryPage(query, urlStr);
		} else {
			for (String queryPage : pageInformation) {
				parseQueryPage(query, queryPage);
			}
		}

	}

	public static void main(String[] args) throws IOException {
		Grabber grabber = new Grabber();
		grabber.init();
		// String urlStr =
		// "http://www.immobilienscout24.de/Suche/S-T/Wohnung-Kauf/Baden-Wuerttemberg/Karlsruhe/Daxlanden";
		String urlStr = "http://www.immobilienscout24.de/Suche/S-T/Wohnung-Kauf/Baden-Wuerttemberg";
		try {
			grabber.processQuery(urlStr);
		} finally {
			grabber.close();
		}
	}

}
