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
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gst.immo.db.ExposeBase;
import com.gst.immo.db.ExposeBaseDAO;
import com.gst.immo.db.Query;
import com.gst.immo.db.QueryDAO;
import com.gst.immo.db.SubQuery;
import com.gst.immo.db.SubQueryDAO;
import com.gst.immo.db.SubqueryToExpose;
import com.gst.immo.db.SubqueryToExposeDAO;
import com.gst.immo.utils.HibernateUtils;

public class Grabber implements Closeable {

	private static final String WWW_IMMOBILIENSCOUT24_DE = "www.immobilienscout24.de";
	public static Logger _log = Logger.getLogger(Grabber.class);
	private SessionFactory _sessionFactory;
	private ClassPathXmlApplicationContext _springContext;
	private QueryDAO _queryDAO;
	private SubQueryDAO _subQueryDAO;
	private ExposeBaseDAO _exposeBaseDAO;
	private SubqueryToExposeDAO _subqueryToExposeDAO;

	public void init() throws IOException {
		_sessionFactory = HibernateUtils.getSessionFactory();
		_springContext = new ClassPathXmlApplicationContext("spring.xml");
		_queryDAO = _springContext.getBean(QueryDAO.class);
		_subQueryDAO = _springContext.getBean(SubQueryDAO.class);
		_exposeBaseDAO = _springContext.getBean(ExposeBaseDAO.class);
		_subqueryToExposeDAO = _springContext.getBean(SubqueryToExposeDAO.class);
	}

	@Override
	public void close() throws IOException {
		_sessionFactory.close();
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
		_subQueryDAO.persist(subQuery);

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
		_log.info("Start parsing exposeId: " + exposeId);
		ExposeBase exposeBase = _exposeBaseDAO.get(exposeId);

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
			_exposeBaseDAO.persist(exposeBase);
		}

		SubqueryToExpose subqueryToExpose = new SubqueryToExpose();
		subqueryToExpose.setExposeBase(exposeBase);
		subqueryToExpose.setSubQuery(subQuery);
		subqueryToExpose.setStatus(status);
		_subqueryToExposeDAO.persist(subqueryToExpose);
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

	private static String buildExposeUrl(long exposeId) {
		return "http://" + WWW_IMMOBILIENSCOUT24_DE + "/expose/" + exposeId;
	}

	private void processQuery(String urlStr) throws IOException {
		Query query = new Query();
		query.setQuery(urlStr);
		_queryDAO.persist(query);

		List<String> pageInformation = extractPageInformation(urlStr);
		if (pageInformation.isEmpty()) {
			parseQueryPage(query, urlStr);
		} else {
			for (String queryPage : pageInformation) {
				parseQueryPage(query, queryPage);
			}
		}

	}

	// private void testHibernate() {
	// SessionFactory factory = HibernateUtils.getSessionFactory();
	// Session session = factory.openSession();
	// session.getTransaction().begin();
	// String sql = "SELECT q.query_id, q.date_time, q.query FROM " +
	// Query.class.getName() + " q";
	// org.hibernate.Query hibQuery = session.createQuery(sql);
	// List<Query> queries = hibQuery.list();
	// queries.forEach(q -> System.out.println(q));
	// session.close();
	// }

	// private void testSpringHibernate() {
	//
	// QueryDAO queryDAO = context.getBean(QueryDAO.class);
	//
	// Query query = new Query();
	// query.setQuery("test2");
	//
	// queryDAO.persist(query);
	//
	// System.out.println("Query::" + query + " id: " + query.getQueryId());
	// }

	public static void main(String[] args) throws IOException {
		Grabber grabber = new Grabber();
		grabber.init();
		String urlStr = "http://www.immobilienscout24.de/Suche/S-T/Wohnung-Kauf/Baden-Wuerttemberg/Karlsruhe/Daxlanden";
		try {
			grabber.processQuery(urlStr);
		} finally {
			grabber.close();
		}
	}

}
