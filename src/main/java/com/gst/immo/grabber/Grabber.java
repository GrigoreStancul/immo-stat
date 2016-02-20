package com.gst.immo.grabber;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gst.immo.db.Query;
import com.gst.immo.db.QueryDAO;
import com.gst.immo.utils.HibernateUtils;

public class Grabber implements Closeable {

	private static final String WWW_IMMOBILIENSCOUT24_DE = "www.immobilienscout24.de";
	public static Logger _log = Logger.getLogger(Grabber.class);
	private SessionFactory _sessionFactory;

	public void init() throws IOException {
		_sessionFactory = HibernateUtils.getSessionFactory();
	}

	@Override
	public void close() throws IOException {
		_sessionFactory.close();
	}

	private void loadPage(String urlStr) throws IOException {
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
		System.out.println(finalHTML);
	}

	private List<String> extractPageInformation(String urlStr) throws IOException {
		List<String> result = new ArrayList<String>();
		Document doc = Jsoup.connect(urlStr).get();
		Elements elements = doc.select("div#pageSelection select option");
		elements.forEach(e -> result.add(buildSearchUrlFromPageLink(e.attr("value"))));
		return result;
	}

	private void parseQueryPage(String urlStr) throws IOException {
		// loadPage(urlStr);
		Document doc = Jsoup.connect(urlStr).get();
		Elements elements = doc.select("ul#resultListItems article div.result-list-entry__data a");
		List<String> links = new ArrayList<String>();
		elements.forEach(e -> links.add(e.attr("href")));

		Set<Long> exposeIds = new HashSet<Long>();
		links.forEach(link -> exposeIds.add(parseExposeLinkId(link)));

		for (Long exposeId : exposeIds) {
			if (exposeId != null) {
				processExpose(exposeId);
			}
		}
	}

	private void processExpose(long exposeId) throws IOException {
		_log.info("Start parsing exposeId: " + exposeId);
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

		List<String> pageInformation = extractPageInformation(urlStr);
		if (pageInformation.isEmpty()) {
			parseQueryPage(urlStr);
		} else {
			for (String queryPage : pageInformation) {
				parseQueryPage(queryPage);
			}
		}

	}

	private void testHibernate() {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.openSession();
		session.getTransaction().begin();
		String sql = "SELECT q.query_id, q.date_time, q.query FROM " + Query.class.getName() + " q";
		org.hibernate.Query hibQuery = session.createQuery(sql);
		List<Query> queries = hibQuery.list();
		queries.forEach(q -> System.out.println(q));
		session.close();
	}

	private void testSpringHibernate() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

		QueryDAO queryDAO = context.getBean(QueryDAO.class);

		Query query = new Query();
		query.setQuery("test");
		query.setDateTime(new Date());

		queryDAO.persist(query);

		System.out.println("Query::" + query + " id: " + query.getQueryId());
	}

	public static void main(String[] args) throws IOException {
		Grabber grabber = new Grabber();
		grabber.init();
		String urlStr = "http://www.immobilienscout24.de/Suche/S-T/Wohnung-Kauf/Baden-Wuerttemberg/Karlsruhe/Daxlanden";
		grabber.testSpringHibernate();
		// grabber.processQuery(urlStr);
		grabber.close();
	}

}
